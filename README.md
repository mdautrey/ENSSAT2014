This guide walks you through the process of creating a simple web application with resources that are protected by Spring Security.

What you'll build
-----------------

You'll build a Spring MVC application that secures the page with a login form backed by a fixed list of users.

What you'll need
----------------

 - About 15 minutes
 - A favorite text editor or IDE
 - [JDK 6][jdk] or later
 - [Gradle 1.8+][gradle] or [Maven 3.0+][mvn]
 - You can also import the code from this guide as well as view the web page directly into [Spring Tool Suite (STS)][gs-sts] and work your way through it from there.

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[gradle]: http://www.gradle.org/
[mvn]: http://maven.apache.org/download.cgi
[gs-sts]: /guides/gs/sts


How to complete this guide
--------------------------

Like all Spring's [Getting Started guides](/guides/gs), you can start from scratch and complete each step, or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Set up the project](#scratch).

To **skip the basics**, do the following:

 - [Download][zip] and unzip the source repository for this guide, or clone it using [Git][u-git]:
`git clone https://github.com/spring-guides/gs-securing-web.git`
 - cd into `gs-securing-web/initial`.
 - Jump ahead to [Set up Spring Security](#initial).

**When you're finished**, you can check your results against the code in `gs-securing-web/complete`.
[zip]: https://github.com/spring-guides/gs-securing-web/archive/master.zip
[u-git]: /understanding/Git


<a name="scratch"></a>
Set up the project
------------------

First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Gradle](http://gradle.org) and [Maven](https://maven.apache.org) is included here. If you're not familiar with either, refer to [Building Java Projects with Gradle](/guides/gs/gradle/) or [Building Java Projects with Maven](/guides/gs/maven).

### Create the directory structure

In a project directory of your choosing, create the following subdirectory structure; for example, with `mkdir -p src/main/java/hello` on *nix systems:

    └── src
        └── main
            └── java
                └── hello


### Create a Gradle build file
Below is the [initial Gradle build file](https://github.com/spring-guides/gs-securing-web/blob/master/initial/build.gradle). But you can also use Maven. The pom.xml file is included [right here](https://github.com/spring-guides/gs-securing-web/blob/master/initial/pom.xml). If you are using [Spring Tool Suite (STS)][gs-sts], you can import the guide directly.

`build.gradle`
```gradle
buildscript {
    repositories {
        maven { url "http://repo.spring.io/libs-snapshot" }
        mavenLocal()
    }
}

apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'idea'

jar {
    baseName = 'gs-securing-web'
    version =  '0.1.0'
}

repositories {
    mavenCentral()
    maven { url "http://repo.spring.io/libs-snapshot" }
}

dependencies {
    compile("org.springframework.boot:spring-boot-starter-web:0.5.0.M4")
    compile("org.springframework.boot:spring-boot-starter-security:0.5.0.M4")
    compile("org.thymeleaf:thymeleaf-spring3:2.0.16")
    testCompile("junit:junit:4.11")
}

task wrapper(type: Wrapper) {
    gradleVersion = '1.8'
}
```
    
[gs-sts]: /guides/gs/sts    

> **Note:** This guide is using [Spring Boot](/guides/gs/spring-boot/).


Create an unsecured web application
-----------------------------------

Before you can apply security to a web application, you need a web application to secure. The steps in this section walk you through creating a very simple web application. Then you secure it with Spring Security in the next section.

The web application includes two simple views: a home page and a "Hello World" page. The home page is defined in the following Thymeleaf template:

`src/main/resources/templates/home.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Spring Security Example</title>
    </head>
    <body>
        <h1>Welcome!</h1>
        
        <p>Click <a th:href="@{/hello}">here</a> to see a greeting.</p>
    </body>
</html>
```

As you can see, this simple view include a link to the page at "/hello". That is defined in the following Thymeleaf template:

`src/main/resources/templates/hello.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1>Hello world!</h1>
        <div><a th:href="@{/logout}">Sign Out</a></div>
    </body>
</html>
```

The web application is based on Spring MVC. Thus you need to configure Spring MVC and set up view controllers to expose these templates. Here's a configuration class for configuring Spring MVC in the application.

`src/main/java/hello/MvcConfig.java`
```java
package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }

}
```

The `addViewControllers()` method (overriding the method of the same name in `WebMvcConfigurerAdapter`) adds four view controllers. Two of the view controllers reference the view whose name is "home" (defined in `home.html`), and another references the view named "hello" (defined in `hello.html`). The fourth view controller references another view named "login". You'll create that view in the next section.

At this point, you could jump ahead to the _[Run the application](#run)_ section and run the application. The logout link won't work, but otherwise it's a functioning Spring MVC application.

With the base simple web application created, you can add security to it.


<a name="initial"></a>
Set up Spring Security
---------------------

Suppose that you want to prevent unauthorized users from viewing the greeting page at "/hello". As it is now, if users click the link on the home page, they see the greeting with no barriers to stop them. You need to add a barrier that forces the user to sign in before seeing that page.

You do that by configuring Spring Security in the application. Here's a security configuration that ensures that only authenticated users can see the secret greeting:

`src/main/java/hello/WebSecurityConfig.java`
```java
package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/home").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .defaultSuccessUrl("/hello")
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
                .permitAll();
    }

    @Override
    protected void registerAuthentication(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
        authManagerBuilder.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }
}
```

The `WebSecurityConfig` class is annotated with `@EnableWebSecurity` to enable Spring Security's web security support. It also extends `WebSecurityConfigurerAdapter` and overrides a couple of its methods to set some specifics of the web security configuration.

The `configure()` method defines with URL paths should be secured and which should not. Specifically, the "/home" path is configured to not require any authentication. All other paths must be authenticated. 

When a user successfully logs in, they will be forwarded to the "/hello" path. There is a custom "/login" page specified by `loginPage()`, and everyone is allowed to view it.

As for the `registerAuthentication()` method, it sets up an in-memory user store with a single user. That user is given a username of "user", a password of "password", and a role of "USER".

All that's left to do is create the login page. There's already a view controller for the "login" view, so you only need to create the login view itself:

`src/main/resources/templates/login.html`
```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Spring Security Example </title>
    </head>
    <body>
        <form th:action="@{/login}" method="post">
            <div><label> User Name : <input type="text" name="username"/> </label></div>
            <div><label> Password: <input type="password" name="password"/> </label></div>
            <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
            <div><input type="submit" value="Sign In"/></div>
        </form>
    </body>
</html>
```

As you can see, this Thymeleaf template simply presents a form that captures a username and password and posts them to "/login". As configured, Spring Security provides a filter that intercepts that request and authenticates the user.


Make the application executable
-------------------------------

Although it is possible to package this service as a traditional _web application archive_ or [WAR][u-war] file for deployment to an external application server, the simpler approach demonstrated below creates a _standalone application_. You package everything in a single, executable JAR file, driven by a good old Java `main()` method. And along the way, you use Spring's support for embedding the [Tomcat][u-tomcat] servlet container as the HTTP runtime, instead of deploying to an external instance.

### Create a main class

`src/main/java/hello/Application.java`
```java
package hello;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Application {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Application.class, args);
    }

}
```

The `main()` method defers to the [`SpringApplication`][] helper class, providing `Application.class` as an argument to its `run()` method. This tells Spring to read the annotation metadata from `Application` and to manage it as a component in the _[Spring application context][u-application-context]_.

The `@ComponentScan` annotation tells Spring to search recursively through the `hello` package and its children for classes marked directly or indirectly with Spring's [`@Component`][] annotation. This directive ensures that Spring finds and registers the `WebConfig` and `WebSecurityConfig`, because they are marked with `@Configuration`, which in turn is a kind of `@Component` annotation. In effect, those configuration classes are also used to configure Spring.

The [`@EnableAutoConfiguration`][] annotation switches on reasonable default behaviors based on the content of your classpath. For example, because the application depends on the embeddable version of Tomcat (tomcat-embed-core.jar), a Tomcat server is set up and configured with reasonable defaults on your behalf. And because the application also depends on Spring MVC (spring-webmvc.jar), a Spring MVC [`DispatcherServlet`][] is configured and registered for you — no `web.xml` necessary! Auto-configuration is a powerful, flexible mechanism. See the [API documentation][`@EnableAutoConfiguration`] for further details.

### Build an executable JAR
Now that your `Application` class is ready, you simply instruct the build system to create a single, executable jar containing everything. This makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

Below are the Gradle steps, but if you are using Maven, you can find the updated pom.xml [right here](https://github.com/spring-guides/gs-securing-web/blob/master/complete/pom.xml) and build it by typing `mvn clean package`.

Update your Gradle `build.gradle` file's `buildscript` section, so that it looks like this:

```groovy
buildscript {
    repositories {
        maven { url "http://repo.spring.io/libs-snapshot" }
        mavenLocal()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:0.5.0.M4")
    }
}
```

Further down inside `build.gradle`, add the following to the list of applied plugins:

```groovy
apply plugin: 'spring-boot'
```
You can see the final version of `build.gradle` [right here]((https://github.com/spring-guides/gs-securing-web/blob/master/complete/build.gradle).

The [Spring Boot gradle plugin][spring-boot-gradle-plugin] collects all the jars on the classpath and builds a single "über-jar", which makes it more convenient to execute and transport your service.
It also searches for the `public static void main()` method to flag as a runnable class.

Now run the following command to produce a single executable JAR file containing all necessary dependency classes and resources:

```sh
$ ./gradlew build
```

If you are using Gradle, you can run the JAR by typing:

```sh
$ java -jar build/libs/gs-securing-web-0.1.0.jar
```

If you are using Maven, you can run the JAR by typing:

```sh
$ java -jar target/gs-securing-web-0.1.0.jar
```

[spring-boot-gradle-plugin]: https://github.com/spring-projects/spring-boot/tree/master/spring-boot-tools/spring-boot-gradle-plugin

> **Note:** The procedure above will create a runnable JAR. You can also opt to [build a classic WAR file](/guides/gs/convert-jar-to-war/) instead.


<a name="run"></a>
Run the service
-------------------
If you are using Gradle, you can run your service at the command line this way:

```sh
$ ./gradlew clean build && java -jar build/libs/gs-securing-web-0.1.0.jar
```

> **Note:** If you are using Maven, you can run your service by typing `mvn clean package && java -jar target/gs-securing-web-0.1.0.jar`.


```
... app starts up ...
```

Once the application starts up, point your browser to [http://localhost:8080](http://localhost:8080). You should see the home page:

![The application's home page](images/home.png)

When you click on the link, it attempts to take you to the greeting page at `/hello`. But because that page is secured and you have not yet logged in, it takes you to the login page:

![The login page](images/login.png)

At the login page, sign in as the test user by entering "user" and "password" for the username and password fields, respectively. Once you submit the login form, you are authenticated and then taken to the greeting page:

![The secured greeting page](images/greeting.png)

If you click on the "logout" link, your authentication is revoked, and you are returned to the home page where you'll need to log in again before seeing the greeting page.


Summary
-------
Congratulations! You have developed a simple web application that is secured with Spring Security.


[u-war]: /understanding/WAR
[u-tomcat]: /understanding/Tomcat
[u-application-context]: /understanding/application-context
[`SpringApplication`]: http://docs.spring.io/spring-boot/docs/0.5.0.M3/api/org/springframework/boot/SpringApplication.html
[`@Component`]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/stereotype/Component.html
[`@EnableAutoConfiguration`]: http://docs.spring.io/spring-boot/docs/0.5.0.M3/api/org/springframework/boot/autoconfigure/EnableAutoConfiguration.html
[`DispatcherServlet`]: http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html
