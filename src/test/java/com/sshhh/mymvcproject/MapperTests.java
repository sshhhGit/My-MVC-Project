package com.sshhh.mymvcproject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sshhh.mymvcproject.domain.MyMvcProjectDTO;
import com.sshhh.mymvcproject.mapper.MyMvcProjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;


@SpringBootTest
class MapperTests {

    @Autowired
    private MyMvcProjectMapper myMvcProjectMapper;

    @Test
    public void testOfInsert() {
        MyMvcProjectDTO params = new MyMvcProjectDTO();
        params.setTitle("1번 게시글 제목");
        params.setContent("1번 게시글 내용");
        params.setWriter("테스터");

        int result = myMvcProjectMapper.insertBoard(params);
        System.out.println("결과는 " + result + "입니다.");
    }

    @Test
    public void testOfSelectDetail() {
        MyMvcProjectDTO board = myMvcProjectMapper.selectBoardDetail((long) 1);
        try {
            String boardJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(board);

            System.out.println("=========================");
            System.out.println(boardJson);
            System.out.println("=========================");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOfUpdate() {
        MyMvcProjectDTO params = new MyMvcProjectDTO();
        params.setTitle("1번 게시글 제목을 수정합니다.");
        params.setContent("1번 게시글 내용을 수정합니다.");
        params.setWriter("홍길동");
        params.setIdx((long) 1);

        int result = myMvcProjectMapper.updateBoard(params);
        if (result == 1) {
            MyMvcProjectDTO board = myMvcProjectMapper.selectBoardDetail((long) 1);
            try {
                String boardJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(board);

                System.out.println("=========================");
                System.out.println(boardJson);
                System.out.println("=========================");

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testOfDelete() {
        int result = myMvcProjectMapper.deleteBoard((long) 1);
        if (result == 1) {
            MyMvcProjectDTO board = myMvcProjectMapper.selectBoardDetail((long) 1);
            try {
                String boardJson = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(board);

                System.out.println("=========================");
                System.out.println(boardJson);
                System.out.println("=========================");

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMultipleInsert() {
        for (int i = 2; i <= 50; i++) {
            MyMvcProjectDTO params = new MyMvcProjectDTO();
            params.setTitle(i + "번 게시글 제목");
            params.setContent(i + "번 게시글 내용");
            params.setWriter(i + "번 게시글 작성자");
            myMvcProjectMapper.insertBoard(params);
        }
    }

/*    @Test
    public void testSelectList() {
        int boardTotalCount = myMvcProjectMapper.selectBoardTotalCount();
        if (boardTotalCount > 0) {
            List<MyMvcProjectDTO> boardList = myMvcProjectMapper.selectBoardList();
            if (CollectionUtils.isEmpty(boardList) == false) {
                for (MyMvcProjectDTO board : boardList) {
                    System.out.println("=========================");
                    System.out.println(board.getTitle());
                    System.out.println(board.getContent());
                    System.out.println(board.getWriter());
                    System.out.println("=========================");
                }
            }
        }
    }*/
}