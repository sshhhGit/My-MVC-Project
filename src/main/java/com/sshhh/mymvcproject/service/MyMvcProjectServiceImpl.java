package com.sshhh.mymvcproject.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sshhh.mymvcproject.domain.AttachDTO;
import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.mapper.AttachMapper;
import com.sshhh.mymvcproject.mapper.MyMvcProjectMapper;
import com.sshhh.mymvcproject.paging.PaginationInfo;
import com.sshhh.mymvcproject.util.FileUtils;

@Service
public class MyMvcProjectServiceImpl implements MyMvcProjectService {

    @Autowired
    private MyMvcProjectMapper mymvcprojectMapper;

    @Autowired
    private AttachMapper attachMapper;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public boolean registerBoard(MyMvcProjectDTO params) {

        int queryResult = 0;

        if (params.getIdx() == null) {
            queryResult = mymvcprojectMapper.insertBoard(params);
        } else {
            queryResult = mymvcprojectMapper.updateBoard(params);

            // 파일이 추가, 삭제, 변경된 경우
            if ("Y".equals(params.getChangeYn())) {
                attachMapper.deleteAttach(params.getIdx());

                // fileIdxs에 포함된 idx를 가지는 파일의 삭제여부를 'N'으로 업데이트
                if (CollectionUtils.isEmpty(params.getFileIdxs()) == false) {
                    attachMapper.undeleteAttach(params.getFileIdxs());
                }
            }
        }

        return (queryResult > 0);
    }

    @Override
    public boolean registerBoard(MyMvcProjectDTO params, MultipartFile[] files) {
        int queryResult = 1;

        if (registerBoard(params) == false) {
            return false;
        }

        List<AttachDTO> fileList = fileUtils.uploadFiles(files, params.getIdx());
        if (CollectionUtils.isEmpty(fileList) == false) {
            queryResult = attachMapper.insertAttach(fileList);
            if (queryResult < 1) {
                queryResult = 0;
            }
        }

        return (queryResult > 0);
    }

    @Override
    public MyMvcProjectDTO getBoardDetail(Long idx) {
        return mymvcprojectMapper.selectBoardDetail(idx);
    }

    @Override
    public boolean deleteBoard(Long idx) {
        int queryResult = 0;

        MyMvcProjectDTO board = mymvcprojectMapper.selectBoardDetail(idx);

        if (board != null && "N".equals(board.getDeleteYn())) {
            queryResult = mymvcprojectMapper.deleteBoard(idx);
        }

        return (queryResult == 1) ? true : false;
    }

    @Override
    public List<MyMvcProjectDTO> getBoardList(MyMvcProjectDTO params) {
        List<MyMvcProjectDTO> boardList = Collections.emptyList();

        int boardTotalCount = mymvcprojectMapper.selectBoardTotalCount(params);

        PaginationInfo paginationInfo = new PaginationInfo(params);
        paginationInfo.setTotalRecordCount(boardTotalCount);

        params.setPaginationInfo(paginationInfo);

        if (boardTotalCount > 0) {
            boardList = mymvcprojectMapper.selectBoardList(params);
        }

        return boardList;
    }

    @Override
    public List<AttachDTO> getAttachFileList(Long boardIdx) {

        int fileTotalCount = attachMapper.selectAttachTotalCount(boardIdx);
        if (fileTotalCount < 1) {
            return Collections.emptyList();
        }
        return attachMapper.selectAttachList(boardIdx);
    }

    @Override
    public AttachDTO getAttachDetail(Long idx) {
        return attachMapper.selectAttachDetail(idx);
    }
}