package hyo.boardexample.common;

import com.nhncorp.lucy.security.xss.XssPreventer;
import hyo.boardexample.domain.Board;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.RemoteException;

/*
init()
웹 컨테이너(톰캣)이 시작될 때 필터 최초 한 번 인스턴스 생성

doFilter()
클라이언트의 요청 시 전/후 처리
FilterChain을 통해 전달

public void destroy()
필터 인스턴스가 제거될 때 실행되는 메서드, 종료하는 기능
 */

public class XssFilter implements Filter {

    /** Multipart 형식으로 데이터를 전달할 때 lucy-xss-filter가 적용되지 않아 따로 처리를 해주는 필터
     *
     * multipart/form-data는 데이터를 가져올때 일반 폼데이터 처럼 getParameter가 아닌 getPart와 getParts 메서드로 데이터를 가져오기때문에,
     * HttpServletRequestWrapper내에서 해당 데이터를 오버라이딩해서 재정의 해야 마찬가지로 필터링이 가능하다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("=======XssFilter 요청========");

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse)  response;

        String uri = servletRequest.getRequestURI();
        String board_title = "";
        String type_no = "";
        String user_id = "";
        String board_no = "";
        String changeYn = "";
        String board_content = "";

        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        MultipartHttpServletRequest multipartRequest = commonsMultipartResolver.resolveMultipart(servletRequest);

        if(uri.equals("/board/insert")) {
            board_title = multipartRequest.getParameter("board_title");
            type_no = multipartRequest.getParameter("type_no");
            user_id = multipartRequest.getParameter("user_id");
        } else if(uri.equals("/board/update")) {
            board_no = multipartRequest.getParameter("board_no");
            type_no = multipartRequest.getParameter("type_no");
            changeYn = multipartRequest.getParameter("changeYn");
        }
        board_content = multipartRequest.getParameter("board_content");

        try {
            Board board = new Board();

            if(uri.equals("/board/insert")) {
                board.setBoard_title(XssPreventer.escape(board_title));
                board.setType_no(Long.parseLong(XssPreventer.escape(type_no)));
                board.setUser_id(XssPreventer.escape(user_id));
            } else if(uri.equals("/board/update")) {
                board.setBoard_no(Long.parseLong(XssPreventer.escape(board_no)));
                board.setType_no(Long.parseLong(XssPreventer.escape(type_no)));
                board.setChangeYn(XssPreventer.escape(changeYn));
            }
            board.setBoard_content(XssPreventer.escape(board_content));

            multipartRequest.setAttribute("board", board);
        }catch (Exception e) {
            throw new RemoteException(e + " : XssFilter 내 Board 매핑 에러");
        }

        chain.doFilter(multipartRequest, response);

        System.out.println("=======XssFilter 종료========");
    }
}
