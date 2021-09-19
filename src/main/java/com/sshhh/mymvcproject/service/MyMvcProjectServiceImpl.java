package com.sshhh.mymvcproject.service;

import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.mapper.MyMvcProjectMapper;
import com.sshhh.mymvcproject.paging.Criteria;
import com.sshhh.mymvcproject.paging.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MyMvcProjectServiceImpl implements MyMvcProjectService {

    @Autowired
    private MyMvcProjectMapper myMvcProjectMapper;

    @Override
    public boolean registerBoard(MyMvcProjectDTO params) {
        int queryResult = 0;

        if (params.getIdx() == null) {
            queryResult = myMvcProjectMapper.insertBoard(params);
        } else {
            queryResult = myMvcProjectMapper.updateBoard(params);
        }

        return (queryResult == 1) ? true : false;
    }

    @Override
    public MyMvcProjectDTO getBoardDetail(Long idx) {
        return myMvcProjectMapper.selectBoardDetail(idx);
    }

    @Override
    public boolean deleteBoard(Long idx) {
        int queryResult = 0;

        MyMvcProjectDTO board = myMvcProjectMapper.selectBoardDetail(idx);

        if (board != null && "N".equals(board.getDeleteYn())) {
            queryResult = myMvcProjectMapper.deleteBoard(idx);
        }

        return (queryResult == 1) ? true : false;
    }

    @Override
    public List<MyMvcProjectDTO> getBoardList(MyMvcProjectDTO params) {
        List<MyMvcProjectDTO> boardList = Collections.emptyList();

        int boardTotalCount = myMvcProjectMapper.selectBoardTotalCount(params);

        PaginationInfo paginationInfo = new PaginationInfo(params);
        paginationInfo.setTotalRecordCount(boardTotalCount);

        params.setPaginationInfo(paginationInfo);

        if (boardTotalCount > 0) {
            boardList = myMvcProjectMapper.selectBoardList(params);
        }

        return boardList;
    }

}