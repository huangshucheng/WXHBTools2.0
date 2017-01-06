package com.junyou.hbks.config;

public class AliConfig {
    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2017010404835533";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    public static final String RSA2_PRIVATE = "";
    public static final String RSA_PRIVATE ="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCaKPXMSkZsPXJbcbJ3bPgUhx1LNlkq5XTmugy98sIL6N0HxS4AQGyavILpNYNBK6U92gY7kfXGQHoKP2g2qFFOiNFYPj/9S3DdXz9asHh3EAyY61xQKpdmvKcXoki2vFeAYthHI9dhf5Rt2OkejZwnKI+79Vh2EhHyL8DlIzjzdAZQvr82ed8sAHOIQiRcqemCxSlWv7FdDWZBdsUbhxq+o8mC1o8LRebRyyBdskaW062llVpJDfeJSCEQvDSxnHUdl2V028b9ZrA5xvKlB74B2z2/gVl76SjNBdIQwDvjxoOR/ipA65+p3gb3qGN809BSgJ03g05eybJo5kZuGORbAgMBAAECggEAM2RkTO6V+4+giAunS1jweydwBJrhrwM/rYtopApB1maTCPwbEcpoak1d9xD36Mn8FFqSqVlHbgggpznsLAyAzoWeBKT8AB0eSy5tmHYsMsUO7gvIgpEwV/ecXKylQQYYxZwuLcktDDvT56rgTGc5H3TONgLcsr/ja7GvxLOxULdgu9df46SnEJFQMVvO7KbqDJuIPBvW93GEDnYoinOvTt5zhvIONrIpJo1qKL4gs9QGJUhQHnWFhKZpJOKF7Me92Mk2ftj5znwXxOmGeZaYQl/f/xHq/S7QoL9208L6QtLxS3Ozznz4l9tQ2ADcKeNmO1aUpPbcpeBH+yuQ6mHVwQKBgQDNe2iJcIDI99r2h6zg+1Vou4W+pVhIbquMWkDRQGZG8yOGcpkC5EnN6gzTWxHRNAR/uJwtZ+roHjTdYEPuZ2zavOKZrj1Fkbt1uZbEepzudZjfUBtiwthElIFhRjabxclCcoxR09GrHSsZsWohc55R2ZyyJNo+bfkp25X5VnWptwKBgQDAD3QRpgx1eYIv0vqvXJMit58pLIdWtRoxpIlkPckRQetIh+EPkJ+sQP/Q1AkNyC3VC9TVz4qwP4jWof25sKtmoL2dp+ENYOndyBIM8h1cbPLxzfVEEVi4izGUp71MbzMbFXMqm/YobJg0AhNG4NARSBcBh1Lly+rmKAjTEZsqfQKBgHVs7NR+Ilq2qb7w0e7QMoLjhw7n1oeuFwLiIOBz+P7au5Z9rstkQnkWI7Y0+P2gyGFk0ntwQ2HawUMxXCDFogNupeBFHma/XW1tXadymnP7aB1V3hCWYJjwdO9t/I8QfG8ont+0OMGcraDFLm1ncTY9MbrsLHw4xIcTIaWXry4JAoGARAS1DJMMEoan7pv/oB9fFIqHWaOlgrW8wxwMdG3VScFYvGUy9bo4cl7KuoB9fF4KrUr1Y4uPCIBXy/uuyhahfUUsIiuUbpKGAL+cdO1CLLuBBtX0KNkj3KefNZB6YBl5tfvQv6Rgd0yOUszxWsa/qX8odqggWbXwSPaguW+gzlkCgYAwg1BhwQvyc4GXyR0H4rBzr+6yDDbQkK9hvH4ktMmBiI8OW1haDrB/JbDQOyTWd6UYcszW6/FFXRj8h+siSwZYefCc+yUUl4S+haxYivSxRwVeyirB08uuM1UXNzA33cl9LQ3NCD5EGuqfal09h8VRYoghhRLoXfpIzMVcVuzbAQ==";

    public static final int SDK_PAY_FLAG = 1;
    public static final int SDK_AUTH_FLAG = 2;
}
