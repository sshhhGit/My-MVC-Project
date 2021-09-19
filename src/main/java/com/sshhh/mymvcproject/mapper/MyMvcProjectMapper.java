package com.sshhh.mymvcproject.mapper;

import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.paging.Criteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MyMvcProjectMapper {

    public int insertBoard(MyMvcProjectDTO params);

    public MyMvcProjectDTO selectBoardDetail(Long idx);

    public int updateBoard(MyMvcProjectDTO params);

    public int deleteBoard(Long idx);

    public List<MyMvcProjectDTO> selectBoardList(MyMvcProjectDTO params);

    public int selectBoardTotalCount(MyMvcProjectDTO params);

}
