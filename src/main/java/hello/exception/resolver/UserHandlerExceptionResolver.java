package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof UserException) {
                log.info("UserException resolver to 400");

                // accept header 꺼내기
                String acceptHeader = request.getHeader("accept");

                // http 응답 상태 세팅
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // accept header 가 application/json 과 같다면
                if ("application/json".equals(acceptHeader)) {

                    HashMap<String, Object> errorResult = new HashMap<>();

                    errorResult.put("ex", ex.getClass()); // 어떤 에러인지 담기
                    errorResult.put("message", ex.getMessage()); // 에러 메세지 담기

                    // json 객체 -> 문자
                    String result = objectMapper.writeValueAsString(errorResult);

                    // response 생성
                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().write(result); // 응답 바디에 데이터 삽입

                    return new ModelAndView(); // 빈 ModelAndView 객체 리턴! -> 예외를 여기서 먹어버리고, 정상을 와스에 리턴
                } else { // acceptHeader 가 application/json 이 아닌 경우
                    // TEXT/HTML 리턴
                    return new ModelAndView("error/500");
                }
            }

       } catch (IOException e) {
            log.info("resolver ex", e);
        }

        return null;
    }
}
