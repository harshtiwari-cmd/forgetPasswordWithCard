//package com.dukhan.forgot.config;
//
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.annotation.Validated;
//
//
//
//@Data
//@Component
//@Validated
//@ConfigurationProperties(prefix = "app")
//public class AppProperties {
//
//    @Valid
//    @NotNull
//    private Language language = new Language();
//
//    @Valid
//    @NotNull
//    private Validation validation = new Validation();
//
//    @Valid
//    @NotNull
//    private Error error = new Error();
//
//    @Valid
//    @NotNull
//    private Sms sms = new Sms();
//
//    @Valid
//    @NotNull
//    private Eai eai = new Eai();
//
//    @Valid
//    @NotNull
//    private Crm crm = new Crm();
//
//    @Valid
//    @NotNull
//    private Mock mock = new Mock();
//
//    @Data
//    public static class Language {
//        private String english;
//        private String arabic;
//        private String defaultLang;
//    }
//
//    @Data
//    public static class Validation {
//        @Valid
//        @NotNull
//        private FieldValidation otp;
//
//        @Valid
//        @NotNull
//        private FieldValidation customerNumber;
//
//        @Valid
//        @NotNull
//        private FieldValidation mobileNumber;
//
//        @Valid
//        @NotNull
//        private FieldValidation transactionDateTime;
//
//        @Valid
//        @NotNull
//        private FieldValidation referenceNum;
//
//        @Data
//        public static class FieldValidation {
//            @NotBlank
//            private String pattern;
//
//            @NotBlank
//            private String message;
//
//            public FieldValidation() {}
//
//            public FieldValidation(String pattern, String message) {
//                this.pattern = pattern;
//                this.message = message;
//            }
//        }
//    }
//
//    @Data
//    public static class Error {
//        private String mqTimeout;
//        private String mqConnection;
//        private String jaxbMarshalling;
//        private String jaxbUnmarshalling;
//        private String mobileRequiredNonCustomer;
//        private String eitherCustomerOrMobile;
//        private String serviceNotFound;
//        private String invalidRequest;
//    }
//
//    @Data
//    public static class Sms {
//        private String serviceNumber;
//        private String customerNonEnglish;
//        private String customerNonArabic;
//    }
//
//    @Data
//    public static class Eai {
//        private Header header = new Header();
//        private Auth auth = new Auth();
//
//        @Data
//        public static class Header {
//            private String client;
//            private String clientChannel;
//            private String msgChannel;
//            private String serviceType;
//            private String serviceVersion;
//        }
//
//        @Data
//        public static class Auth {
//            private String user;
//            private String password;
//        }
//    }
//
//    @Data
//    public static class Crm {
//        private String defaultTitle;
//        private String defaultSourceChannel;
//        private String defaultDepartment;
//        private String defaultAssignee;
//        private Priority priority = new Priority();
//
//        @Data
//        public static class Priority {
//            private String applyProduct;
//            private String callBack;
//        }
//    }
//
//    @Data
//    public static class Mock {
//        private boolean isMockResponse;
//    }
//}
