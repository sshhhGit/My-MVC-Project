package com.sshhh.mymvcproject.controller;

import com.sshhh.mymvcproject.constant.Method;
import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.paging.Criteria;
import com.sshhh.mymvcproject.service.MyMvcProjectService;
import com.sshhh.mymvcproject.util.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        }

        return "board/write";
    }

    @PostMapping(value = "/board/register.do")
    public String registerBoard(@ModelAttribute("params") final MyMvcProjectDTO params, Model model) {
        Map<String, Object> pagingParams = getPagingParams(params);
        try {
            boolean isRegistered = myMvcProjectService.registerBoard(params);
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

}
