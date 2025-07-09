plugins {
    id("java")
    id("application")
}

group = "org.lst"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Core dependencies
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.la4j:la4j:0.6.0")
    
    // HTTP client
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    
    // RxJava
    implementation("io.reactivex:rxjava:1.3.8")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("org.lst.trading.main.BacktestMain")
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}