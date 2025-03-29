package com.keakimleang.digital_menu.constants;

public class SecurityConstant {
    public static final String[] ANONYMOUS_PATH = {
            "/error/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api-docs/**",
            "/aggregate/**",

            "/actuator/prometheus",
            "/actuator/metrics/**",
            "/actuator/health/**",

            "/api/v1/files/**",

            "/api/v1/menus/of-store/{storeId}/all/with",

            "/api/v1/stores/{slug}/**",
            "/api/v1/categories/list/**",

            "/api/v1/orders/create",
            "/api/v1/orders/{id}/menus",

            "/api/v1/feedbacks/create",

            "/api/v1/auth/**",
            "/api/v1/users/{id}/find",
    };
}
