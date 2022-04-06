package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 컨트롤러에서 예외가 발생하면, 디스패처 서블릿(프론트 컨트롤러)으로 예외가 전달되는데,
 * 이 때 HandlerExceptionResolver 을 사용하면
 * 에러 결과를 커스텀(오류 메시지, 형식등을  API마다 다르게 처리) 할 수 있다.
 *
 * ExceptionResolver 가 ModelAndView 를 반환하는 이유는
 * 마치 try, catch를 하듯이, Exception 을
 * 처리해서 정상 흐름 처럼 변경하는 것이 목적
 *
 * 이름 그대로 Exception 을 Resolver(해결)하는 것이 목적
 */
@Slf4j
public class MyHandlerException implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        /**
         * IllegalArgumentException 이 발생하면 response.sendError(400) 를 호출해서 HTTP
         * 상태 코드를 400으로 지정하고, 빈 ModelAndView 를 반환
         */
        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");

                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                return new ModelAndView();
                // 새로운 ModelAndView 를 생성하고 빈 값으로 넘기면
                // 와스까지 정상 흐름으로 리턴된다.
                // -> 예외를 여기서 잡아서 먹어버리고, 와스는 sendError 에 정의 해 놓은
                //    SC_BAD_REQUEST 에러를 클라이언트에게 전달한다.
            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
