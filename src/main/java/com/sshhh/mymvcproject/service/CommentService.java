package com.sshhh.mymvcproject.service;

import java.util.List;

import com.sshhh.mymvcproject.domain.CommentDTO;

public interface CommentService {

    public boolean registerComment(CommentDTO params);

    public boolean deleteComment(Long idx);

    public List<CommentDTO> getCommentList(CommentDTO params);

}