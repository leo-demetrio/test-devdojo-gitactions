package project.base.studiesspring.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPostRequestBody {

    @NotEmpty(message = "The product name cannot be empty")
    @Schema(description = "Name of product", example = "Galaxy A20", required = true)
    private String name;
}
