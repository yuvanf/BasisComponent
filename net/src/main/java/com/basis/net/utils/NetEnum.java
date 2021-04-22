package com.basis.net.utils;


public enum NetEnum {

    NetSuccess {
        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String getName() {
            return "请求成功";
        }

        @Override
        public String getMessage() {
            return "";
        }
    },
    TimeOutException {
        @Override
        public int getId() {
            return 1;
        }

        @Override
        public String getName() {
            return "请求超时";
        }

        @Override
        public String getMessage() {
            return "当前网络不稳定，请稍后重试";
        }

    },
    DisconnectException {
        @Override
        public int getId() {
            return 2;
        }

        @Override
        public String getName() {
            return "没有网络";
        }

        @Override
        public String getMessage() {
            return "网络连接不可用,请稍候重试";
        }
    },
    UnKnowHostException {
        @Override
        public int getId() {
            return 3;
        }

        @Override
        public String getName() {
            return "无法解析该域名异常";
        }

        @Override
        public String getMessage() {
            return "当前网络不稳定，请稍后重试";
        }
    },
    ParseException {
        @Override
        public int getId() {
            return 4;
        }

        @Override
        public String getName() {
            return "请求出错，请稍后再试！（400500）";
        }

        @Override
        public String getMessage() {
            return "当前网络不稳定，请稍后重试";
        }
    },
    NetException {
        @Override
        public int getId() {
            return 5;
        }

        @Override
        public String getName() {
            return "网络异常";
        }

        @Override
        public String getMessage() {
            return "当前网络不稳定，请稍后重试";
        }
    },
    ApiException {
        @Override
        public int getId() {
            return 6;
        }

        @Override
        public String getName() {
            return "ApiException";
        }

        @Override
        public String getMessage() {
            return "";
        }
    },
    ServerException {
        @Override
        public int getId() {
            return 7;
        }

        @Override
        public String getName() {
            return "ServerException";
        }

        @Override
        public String getMessage() {
            return "";
        }
    },
    TokenException {
        @Override
        public int getId() {
            return 8;
        }

        @Override
        public String getName() {
            return "TokenException";
        }

        @Override
        public String getMessage() {
            return "正在重新为您获取相关数据，稍等片刻点击重试！";
        }
    }, CONNECTException {
        @Override
        public int getId() {
            return 9;
        }

        @Override
        public String getName() {
            return "CONNECTException";
        }

        @Override
        public String getMessage() {
            return "当前网络不稳定，请稍后重试";
        }
    },

    FORBIDDEN_CONNECT {
        @Override
        public int getId() {
            return 9;
        }

        @Override
        public String getName() {
            return "FORBIDDEN_CONNECT";
        }

        @Override
        public String getMessage() {
            return "禁止访问,请联系客服";
        }
    },

    SOCKET_CONNECT {
        @Override
        public int getId() {
            return 10;
        }

        @Override
        public String getName() {
            return "SOCKET_CONNECT";
        }

        @Override
        public String getMessage() {
            return "请检查USB是否连接，电脑客户端是否打开";
        }
    };


    public static String getName(int id) {
        for (NetEnum netEnum : NetEnum.values()) {
            if (id == netEnum.getId()) {
                return netEnum.getName();
            }
        }
        return TimeOutException.getName();
    }

    public static String getMessage(int id) {
        for (NetEnum netEnum : NetEnum.values()) {
            if (id == netEnum.getId()) {
                return netEnum.getMessage();
            }
        }
        return TimeOutException.getMessage();
    }

    public static boolean isSuccess(int id) {
        for (NetEnum netEnum : NetEnum.values()) {
            if (id == NetSuccess.getId()) {
                return true;
            }
        }
        return false;
    }

    public abstract int getId();

    public abstract String getName();

    public abstract String getMessage();


}
