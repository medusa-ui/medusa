package io.getmedusa.medusa.sample;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ConstraintViolations;
import am.ik.yavi.core.Validator;
import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.List;

@UIEventPage(path = "/validation", file = "/pages/validation.html")
public class ValidationController {

    public List<Attribute> createUser(User user) {
        ConstraintViolations violations = User.userValidator.validate(user);
        System.out.printf("user: %s\nviolations: %s%n", user, violations.details());
        return List.of(
                new Attribute("success", violations.isValid()),
                new Attribute("violations", violations.details()),
                new Attribute("user", user)
        );
    }

    public record User(String nickname, String email){
       public static Validator<User> userValidator =
               ValidatorBuilder.<User>of()
                       .constraint(User::nickname,"nickname", name -> name.notBlank().greaterThan(4).pattern("^\\s*\\S+\\s*$").message("should not contains spaces"))
                       .constraint(User::email,"email", email -> email.notBlank().email())
                       .build();
    }
}
