package com.reggie.config;

public class AlipayConfig {
    public static final String APP_ID = "2021000122623177";
    // 应用私钥
    public static final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYvEhIw3/WyjdCr8oaP3k989FJIOKzrhtoS6pZGpkewuIO2G1sgofKlJsH1xSFhyOuIPjfYjuQMP0OTrQTDsBUQP9oVv1hlSupbxh8Y6aIfLGPOyYtwKiVabGkqYIkgCjCGgmHdlIEfPDcpjkpZjHTceBmevPa2hdvFbalXEfNAjyn0HdW8HzP65dQZfczrGAaftIj7HQb6S7an7owjutcBc0M4gPqtEfslnpnLM6BcgxZHliS5dMFdn5htszz2E4aCP8jy15xCq7iPcRwQsQA/nGtRDTNhd/9G1K5Dk2PaAasc0Buerz+xYv9WKH5WPxY4qI6vVt316r99zY5aTPfAgMBAAECggEARRtkBXBXpoLmkAjkBA5WOF7agiEcEjqj8gPSdvCe+ZkqGZd5o1BISrQElX6OrYAg98e5c7GJy8supMudlMXm5fhm1/oMOZv6HnbC+H1KO5VX7bvQoYD7hmdFVoDP/t3yEykeyKkToS46+UtIr9+qicaLxzK4pXxqf/uuIZHoduxp/c2at5zUN4F3C/GJm6aAIWl0CsimWgP9lOxjv0i1KPRs+zrRKk/qcqEAnKJtomeZ6amzcyoji1fd6UaWCWXUwNMYH9rCQeGcNmwzCerbBkZspaSe/Y3e9SneR2U7j38J4oTWCm/tbC8Tx/A+e3g95t3cITxts0OIVxhxlSOR8QKBgQDQi3NE5ZOxqRREvoh5wV3kzZzt1GQAmdL8mJX4vaG24llUnQi6jc2GAgEeN8yJ0oUawu/7togY+yzwkaaPfoPQwFgbckaxrP6RAkDHH5X/Vn4StX0e4TsplR9CRrXLG+z8p2sLPT8UmixsNq30ZBqzj5J/XUlISQUJcz+mmPkliQKBgQC7fblaE2/xhftK0oFnVdVXtNjME0RyRshJBkDmEKsWkqT8bJHlMxKoyf91vhCoyV3zDVBXjXQkGELKvpyJDPkhxb7MRaOQPq7VklrjhKXI03q1UQRDepXViYR2/4Cr6yy0+CLwoApR/+ne44tleO/8mPge76W5lx5LtIv1I62cJwKBgAv0gpYnhxD/PV83gQFh2W0dcGqd62Gxb8v1P1u0G0otratRGUgAixCsmr3NuxcHj4PaOwG2FBVCiO/9mYvOV+YmsKZaJb1XynwroeUIZYPuxGl0KAkWJlsa5mDZRuCu6CYPvu41A6G9uOA3Pj+tYW57w40zntja/sPs7HAzgszpAoGAdMD+6mvOOv3vFZHuyVW3C+3vT230IqGXWXoOcfYRRHY/TX2igLMEPJ3FeiHIqAL1gURaMptAprWA6lY2jlXI0lIc1YHMMCgIobCI7sOMpXmkIUL5P7gXlCkVlzDWhLhnV65pu6Q9qQCPN6BFDwR3lboAUOt1LRRA5m3g9d+o3eMCgYEAkElWTP96JuDJNLKUXBJF0eCf/LUKaNNJop7PVdTmBdQ9+4EPd0L+chME7KkN0cy/1nufrbgsADUC7Y1ehoJdov43CcxDPyuzAvWX2K/j6CKchu79rYouFsETqtVs+QtQV+331LksVs7jWyB3hibl7C8k56d4x/SeWixXw4sGmR8=";
    public static final String CHARSET = "UTF-8";
    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhZFa6rfD8fyPQi5kGm4LRWZksQhqZfJsRywCQ4bJiJGoPKkViBA72mCedSD0PrU5jubt8klEIz+uu7as5zrG3WEQxtmH1KfxEb342AqI2pd9ulDQvCpBPe326u7xAiC4T7i+oU+QLsrZ0Jz4Ksa1+lJPCeFKnlVLueHId5Y3PwLcm5+Ou3ni1YO+WqdWKXv8DpV3JbRsKNDIH30FPvwoDuiD7zckdRuX6/6/5pqlu2qwG5vFhP0w3E903WLCEgxrz7z954PS06UDqN7S5DCkh+p5UwuTxXhI9hoY8jJRJqEDWMqGGyilB6SPCrzK2ts8iyI1ehtMbGcevXdIkFsP2wIDAQAB";
    // 这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    public static final String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    public static final String FORMAT = "JSON";
    // 签名方式
    public static final String SIGN_TYPE = "RSA2";
    // 支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    public static final String NOTIFY_URL = "http://127.0.0.1/notifyUrl";
    // 支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    public static final String RETURN_URL = "http://localhost:8080/pay/return";
}
