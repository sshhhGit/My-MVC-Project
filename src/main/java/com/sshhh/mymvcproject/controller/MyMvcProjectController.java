package com.sshhh.mymvcproject.controller;

import com.sshhh.mymvcproject.constant.Method;
import com.sshhh.mymvcproject.domain.AttachDTO;
import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.paging.Criteria;
import com.sshhh.mymvcproject.service.MyMvcProjectService;
import com.sshhh.mymvcproject.util.UiUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MyMvcProjectController extends UiUtils {

    @Autowired
    private MyMvcProjectService myMvcProjectService;

    @GetMapping(value = "/index.do")
    public String index() {

        return "/index";
    }

    @GetMapping(value = "/board/write.do")
    public String openBoardWrite(@ModelAttribute("params") MyMvcProjectDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            model.addAttribute("board", new MyMvcProjectDTO());
        } else {
            MyMvcProjectDTO board = myMvcProjectService.getBoardDetail(idx);
            if (board == null || "Y".equals(board.getDeleteYn())) {
                return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/board/list.do", Method.GET, null, model);
            }
            model.addAttribute("board", board);

            List<AttachDTO> fileList = myMvcProjectService.getAttachFileList(idx);
            model.addAttribute("fileList", fileList);
        }

        return "board/write";
    }

    @PostMapping(value = "/board/register.do")
    public String registerBoard(final MyMvcProjectDTO params, final MultipartFile[] files, Model model) {
        Map<String, Object> pagingParams = getPagingParams(params);
        try {
            boolean isRegistered = myMvcProjectService.registerBoard(params, files);
            if (isRegistered == false) {
                return showMessageWithRedirect("게시글 등록에 실패하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
            }
        } catch (DataAccessException e) {
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);

        } catch (Exception e) {
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
        }

        return showMessageWithRedirect("게시글 등록이 완료되었습니다.", "/board/list.do", Method.GET, pagingParams, model);
    }

    @GetMapping(value = "/board/list.do")
    public String openBoardList(@ModelAttribute("params") MyMvcProjectDTO params, Model model) {
        List<MyMvcProjectDTO> boardList = myMvcProjectService.getBoardList(params);
        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    @GetMapping(value = "/board/view.do")
    public String openBoardDetail(@ModelAttribute("params") MyMvcProjectDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
        }

        MyMvcProjectDTO board = myMvcProjectService.getBoardDetail(idx);
        if (board == null || "Y".equals(board.getDeleteYn())) {
            return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/board/list.do", Method.GET, null, model);
        }
        model.addAttribute("board", board);

        List<AttachDTO> fileList = myMvcProjectService.getAttachFileList(idx); // 추가된 로직
        model.addAttribute("fileList", fileList); // 추가된 로직

        return "board/view";
    }

    public Map<String, Object> getPagingParams(Criteria criteria) {

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("currentPageNo", criteria.getCurrentPageNo());
        params.put("recordsPerPage", criteria.getRecordsPerPage());
        params.put("pageSize", criteria.getPageSize());
        params.put("searchType", criteria.getSearchType());
        params.put("searchKeyword", criteria.getSearchKeyword());

        return params;
    }

    @PostMapping(value = "/board/delete.do")
    public String deleteBoard(@ModelAttribute("params") MyMvcProjectDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
        }

        Map<String, Object> pagingParams = getPagingParams(params);
        try {
            boolean isDeleted = myMvcProjectService.deleteBoard(idx);
            if (isDeleted == false) {
                return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
            }
        } catch (DataAccessException e) {
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);

        } catch (Exception e) {
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
        }

        return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/board/list.do", Method.GET, pagingParams, model);
    }

    @GetMapping("/board/download.do")
    public void downloadAttachFile(@RequestParam(value = "idx", required = false) final Long idx, Model model, HttpServletResponse response) {

        if (idx == null) throw new RuntimeException("올바르지 않은 접근입니다.");

        AttachDTO fileInfo = myMvcProjectService.getAttachDetail(idx);
        if (fileInfo == null || "Y".equals(fileInfo.getDeleteYn())) {
            throw new RuntimeException("파일 정보를 찾을 수 없습니다.");
        }

        String uploadDate = fileInfo.getInsertTime().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String uploadPath = Paths.get("D:", "back", "upload", uploadDate).toString();

        String filename = fileInfo.getOriginalName();
        File file = new File(uploadPath, fileInfo.getSaveName());

        try {
            byte[] data = FileUtils.readFileToByteArray(file);
            response.setContentType("application/octet-stream");
            response.setContentLength(data.length);
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(filename, "UTF-8") + "\";");

            response.getOutputStream().write(data);
            response.getOutputStream().flush();
            response.getOutputStream().close();

        } catch (IOException e) {
            throw new RuntimeException("파일 다운로드에 실패하였습니다.");

        } catch (Exception e) {
            throw new RuntimeException("시스템에 문제가 발생하였습니다.");
        }
    }
}
