package com.bitcoin.juwan.api3;
/**
 * FileName：ViewBinder
 * Create By：liumengqiang
 * Description：TODO
 */
public interface ViewBinder<T> {
    void bindView(T host, Object o, ViewFinder viewFinder);

    void unBindView(T host);
}
