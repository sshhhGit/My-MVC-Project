package com.sshhh.mymvcproject.service;

import com.sshhh.mymvcproject.domain.AttachDTO;
import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.paging.Criteria;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MyMvcProjectService {

    public boolean registerBoard(MyMvcProjectDTO params);

    public boolean registerBoard(MyMvcProjectDTO params, MultipartFile[] files);

    public MyMvcProjectDTO getBoardDetail(Long idx);

    public boolean deleteBoard(Long idx);

    public List<MyMvcProjectDTO> getBoardList(MyMvcProjectDTO params);

    public List<AttachDTO> getAttachFileList(Long boardIdx);

    public AttachDTO getAttachDetail(Long idx);
}
