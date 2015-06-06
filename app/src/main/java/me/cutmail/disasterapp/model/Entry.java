package me.cutmail.disasterapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entry implements Serializable {

    private String title;
    private String url;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Entry(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public static List<Entry> getEntries() {
        return new ArrayList<Entry>() {{
            add(new Entry("docomo 災害用伝言板", "http://dengon.docomo.ne.jp/top.cgi"));
            add(new Entry("au 災害用伝言板", "http://dengon.ezweb.ne.jp/"));
            add(new Entry("softbank 災害用伝言板", "http://dengon.softbank.ne.jp/J"));
            add(new Entry("ワイモバイル 災害用伝言板", "http://dengon.willcom-inc.com/dengon/Top.do"));
            add(new Entry("気象庁", "http://www.jma.go.jp/jp/quake/"));
            add(new Entry("Yahoo! 天気・災害", "http://weather.yahoo.co.jp/weather/"));
            add(new Entry("NHKニュース - Twitter", "https://mobile.twitter.com/nhk_news"));
            add(new Entry("NHK広報局 - Twitter", "https://mobile.twitter.com/nhk_pr/"));
            add(new Entry("総務省消防庁 - Twitter", "https://mobile.twitter.com/FDMA_JAPAN"));
            add(new Entry("首相官邸（災害情報）- Twitter", "https://mobile.twitter.com/Kantei_Saigai"));
            add(new Entry("東京都23区避難場所", "http://cgi.mobile.metro.tokyo.jp/aps/tosei/bousai/hinan.html"));
            add(new Entry("東京都内避難所 - Googleマップ", "https://www.google.com/maps/d/viewer?brcurrent=3,0x605d1b87f02e57e7:0x2e01618b22571b89,0&ie=UTF8&msa=0&z=12&hl=ja&mid=zmxOP8eNul4g.k1E4ZBHNp-2U"));
            add(new Entry("全国避難所一覧", "http://www.animal-navi.com/navi/map/map.html"));
            add(new Entry("鉄道運行情報", "http://www.tetsudo.com/traffic/"));
        }};
    }
}
