package com.github.pondcat.webdemo.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * springfox-swagger2配置, 非生产环境启用
 *
 * @author gejian at 2018/5/22 8:53
 */
@Profile({ "!prod" })
@Configuration
@EnableSwagger2
@Import({ BeanValidatorPluginsConfiguration.class })
public class Swagger2Config {

	@Autowired
	private TypeResolver typeResolver;

	private static List<Parameter> commonParameters = null;

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Springfox demo API")
				.description("this is a springfox api demo").build();
	}

	@Bean
	public Docket controller() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("api-web")
				.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.ant("/web/**")).build().pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(newRule(
						typeResolver.resolve(DeferredResult.class,
								typeResolver.resolve(ResponseEntity.class,
										WildcardType.class)),
						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false).enableUrlTemplating(true)
				.globalOperationParameters(operationParameters())
				.tags(new Tag("Pet Service", "All apis relating to pets"))
		// .additionalModels(typeResolver.resolve(AdditionalModel.class))
		;
	}

	private List<Parameter> operationParameters() {
		if (commonParameters == null) {
			ModelRef modelRef = new ModelRef("string");
			commonParameters = newArrayList(
					parameter("ver", "客户端版本号", modelRef, "cookie", false),
					parameter("ver", "客户端版本号", modelRef, "header", false),
					parameter("ver", "客户端版本号", modelRef, "query", false),
					parameter("token", "令牌", modelRef, "cookie", false),
					parameter("token", "令牌", modelRef, "header", false),
					parameter("token", "令牌", modelRef, "query", false));
		}
		return commonParameters;
	}

	private Parameter parameter(String name, String description, ModelRef modelRef,
			String parameterType, boolean required) {
		return new ParameterBuilder().name(name).description(description)
				.modelRef(modelRef).parameterType(parameterType).required(required)
				.build();
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("api-projectName")
				.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.ant("/api/**")).build().pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(newRule(
						typeResolver.resolve(DeferredResult.class,
								typeResolver.resolve(ResponseEntity.class,
										WildcardType.class)),
						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false).enableUrlTemplating(true)
				.globalOperationParameters(
						newArrayList(new ParameterBuilder().name("someGlobalParameter")
								.description("Description of someGlobalParameter")
								.modelRef(new ModelRef("string")).parameterType("query")
								.required(true).build()))
				.tags(new Tag("Pet Service", "All apis relating to pets"))
		// .additionalModels(typeResolver.resolve(AdditionalModel.class))
		;
	}

	@Bean
	public Docket openapi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("openapi-projectName")
				.apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.ant("/openapi/**")).build().pathMapping("/")
				.directModelSubstitute(LocalDate.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class)
				.alternateTypeRules(newRule(
						typeResolver.resolve(DeferredResult.class,
								typeResolver.resolve(ResponseEntity.class,
										WildcardType.class)),
						typeResolver.resolve(WildcardType.class)))
				.useDefaultResponseMessages(false).enableUrlTemplating(true)
				.globalOperationParameters(
						newArrayList(new ParameterBuilder().name("someGlobalParameter")
								.description("Description of someGlobalParameter")
								.modelRef(new ModelRef("string")).parameterType("query")
								.required(true).build()))
				.tags(new Tag("Pet Service", "All apis relating to pets"))
		// .additionalModels(typeResolver.resolve(AdditionalModel.class))
		;
	}

	@Bean
	UiConfiguration uiConfig() {
		return UiConfigurationBuilder.builder().deepLinking(true)
				.displayOperationId(false).defaultModelsExpandDepth(1)
				.defaultModelExpandDepth(1).defaultModelRendering(ModelRendering.EXAMPLE)
				.displayRequestDuration(false).docExpansion(DocExpansion.LIST)
				.filter(false).maxDisplayedTags(null)
				.operationsSorter(OperationsSorter.ALPHA).showExtensions(false)
				.tagsSorter(TagsSorter.ALPHA)
				.supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
				.validatorUrl(null).build();
	}

}
