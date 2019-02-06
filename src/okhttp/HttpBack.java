package okhttp;

public interface HttpBack<ResultObj> {
    /**
     * 请求数据成功之后的回调
     * <p>
     * 返回对象
     */
    void loadSucceed(ResultObj result);

    /**
     * 请求数据失败之后的回调
     *
     * @param error 错误信息
     */
    void loadFailed(String error);
}
