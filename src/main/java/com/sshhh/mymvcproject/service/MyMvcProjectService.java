package com.sshhh.mymvcproject.service;

import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.paging.Criteria;

import java.util.List;

public interface MyMvcProjectService {

    public boolean registerBoard(MyMvcProjectDTO params);

    public MyMvcProjectDTO getBoardDetail(Long idx);

    public boolean deleteBoard(Long idx);

    public List<MyMvcProjectDTO> getBoardList(MyMvcProjectDTO params);
}
