package io.igileintelligence.ppmtool.validator;

import io.igileintelligence.ppmtool.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            errors.rejectValue("password", "Length", "Password must be at least" +
                    " 6 characters");
        }
        if (user.getConfirmPassword() == null || !user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Passwords must match");
        }
    }
}
