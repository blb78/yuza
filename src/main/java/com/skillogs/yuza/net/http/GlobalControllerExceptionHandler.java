package com.skillogs.yuza.net.http;

import com.skillogs.yuza.net.exception.ApiException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
class GlobalControllerExceptionHandler {


    @ExceptionHandler(ApiException.class)
    public ModelAndView handleApiException(ApiException ex) {

        ModelAndView model = new ModelAndView("error/generic_error");
        model.addObject("errCode", ex.getErrCode());
        model.addObject("errMsg", ex.getErrMsg());

        return model;

    }

}
