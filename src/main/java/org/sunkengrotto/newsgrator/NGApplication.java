package org.sunkengrotto.newsgrator;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(info = @Info(title = "newsgrator API", version = "1.0.0", license = @License(name = "MIT", url = "https://mit-license.org/")))
public class NGApplication extends Application {
}
