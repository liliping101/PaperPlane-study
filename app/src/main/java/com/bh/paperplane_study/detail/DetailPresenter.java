package com.bh.paperplane_study.detail;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Html;
import android.webkit.WebView;

import com.bh.paperplane_study.R;
import com.bh.paperplane_study.application.App;
import com.bh.paperplane_study.bean.BeanType;
import com.bh.paperplane_study.bean.DoubanMomentNews;
import com.bh.paperplane_study.bean.DoubanMomentStory;
import com.bh.paperplane_study.bean.ZhihuDailyStory;
import com.bh.paperplane_study.entity.HistoryEntity;
import com.bh.paperplane_study.gen.DaoSession;
import com.bh.paperplane_study.gen.HistoryEntityDao;
import com.bh.paperplane_study.http.HttpMethods;
import com.bh.paperplane_study.innerbrowser.InnerBrowserActivity;
import com.bh.paperplane_study.util.Api;
import com.bh.paperplane_study.util.NetworkState;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class DetailPresenter implements DetailContract.Presenter {

    private DetailContract.View view;
    private Context context;

    private ZhihuDailyStory zhihuDailyStory;
    private String guokrStory;
    private DoubanMomentStory doubanMomentStory;
    private HttpMethods httpMethods;
    private SharedPreferences sp;
    private RxQuery<HistoryEntity> historysQuery;
    private DaoSession daoSession;
    private HistoryEntityDao historyEntityDao;

    private Gson gson;

    // the four data come from the intent extra
    private BeanType type;
    private int id;
    private String title;
    private String coverUrl;

    public void setType(BeanType type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public DetailPresenter(@NonNull Context context, @NonNull DetailContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        sp = context.getSharedPreferences("user_setting", MODE_PRIVATE);
        daoSession = ((App) context.getApplicationContext()).getDaoSession();
        historyEntityDao = daoSession.getHistoryEntityDao();
        gson = new Gson();
        httpMethods = HttpMethods.getInstance();
    }


    @Override
    public void openInBrowser() {
        if (checkNull()) {
            view.showLoadingError();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            switch (type) {
                case TYPE_ZHIHU:
                    intent.setData(Uri.parse(zhihuDailyStory.getShare_url()));
                    break;
                case TYPE_GUOKR:
                    intent.setData(Uri.parse(Api.GUOKR_ARTICLE_LINK_V1 + id));
                    break;
                case TYPE_DOUBAN:
                    intent.setData(Uri.parse(doubanMomentStory.getShort_url()));
            }

            context.startActivity(intent);

        } catch (android.content.ActivityNotFoundException ex){
            view.showBrowserNotFoundError();
        }
    }

    @Override
    public void shareAsText() {
        if (checkNull()) {
            view.showSharingError();
            return;
        }

        try {
            Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain");
            String shareText = "" + title + " ";

            switch (type) {
                case TYPE_ZHIHU:
                    shareText += zhihuDailyStory.getShare_url();
                    break;
                case TYPE_GUOKR:
                    shareText += Api.GUOKR_ARTICLE_LINK_V1 + id;
                    break;
                case TYPE_DOUBAN:
                    shareText += doubanMomentStory.getShort_url();
            }

            shareText = shareText + "\t\t\t" + context.getString(R.string.share_extra);

            shareIntent.putExtra(Intent.EXTRA_TEXT,shareText);
            context.startActivity(Intent.createChooser(shareIntent,context.getString(R.string.share_to)));
        } catch (android.content.ActivityNotFoundException ex){
            view.showLoadingError();
        }

    }

    @Override
    public void openUrl(WebView webView, String url) {
        if (sp.getBoolean("in_app_browser",false)) {
            Intent intent = new Intent(context, InnerBrowserActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        } else {
            try{
                context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            } catch (android.content.ActivityNotFoundException ex){
                view.showBrowserNotFoundError();
            }

        }
    }

    @Override
    public void copyText() {
        if (checkNull()) {
            view.showCopyTextError();
            return;
        }

        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = null;
        switch (type) {
            case TYPE_ZHIHU:
                clipData = ClipData.newPlainText("text", Html.fromHtml(title + "\n" + zhihuDailyStory.getBody()).toString());
                break;
            case TYPE_GUOKR:
                clipData = ClipData.newPlainText("text", Html.fromHtml(guokrStory).toString());
                break;
            case TYPE_DOUBAN:
                clipData = ClipData.newPlainText("text", Html.fromHtml(title + "\n" + doubanMomentStory.getContent()).toString());
        }
        manager.setPrimaryClip(clipData);
        view.showTextCopied();

    }

    @Override
    public void copyLink() {
        if (checkNull()) {
            view.showCopyTextError();
            return;
        }

        ClipboardManager manager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = null;
        switch (type) {
            case TYPE_ZHIHU:
                clipData = ClipData.newPlainText("text", Html.fromHtml(zhihuDailyStory.getShare_url()).toString());
                break;
            case TYPE_GUOKR:
                clipData = ClipData.newPlainText("text", Html.fromHtml(Api.GUOKR_ARTICLE_LINK_V1 + id).toString());
                break;
            case TYPE_DOUBAN:
                clipData = ClipData.newPlainText("text", Html.fromHtml(doubanMomentStory.getOriginal_url()).toString());
        }
        manager.setPrimaryClip(clipData);
        view.showTextCopied();

    }

    @Override
    public void addToOrDeleteFromBookmarks() {
        String tmpTable = "";
        String tmpId = "";
        switch (type) {
            case TYPE_ZHIHU:
                tmpTable = "Zhihu";
                tmpId = "zhihu_id";
                break;
            case TYPE_GUOKR:
                tmpTable = "Guokr";
                tmpId = "guokr_id";
                break;
            case TYPE_DOUBAN:
                tmpTable = "Douban";
                tmpId = "douban_id";
                break;
            default:
                break;
        }

        if (queryIfIsBookmarked()) {
            // delete
            // update Zhihu set bookmark = 0 where zhihu_id = id
            HistoryEntity entity = historyEntityDao.queryBuilder()
                    .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(type.name())).build().unique();
            if (entity != null) {
                entity.setBookmark(0);
                historyEntityDao.update(entity);
            }
            view.showDeletedFromBookmarks();
        } else {
            // add
            // update Zhihu set bookmark = 1 where zhihu_id = id
            HistoryEntity entity = historyEntityDao.queryBuilder()
                    .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(type.name())).build().unique();
            if (entity != null) {
                entity.setBookmark(1);
                historyEntityDao.update(entity);
            }

            view.showAddedToBookmarks();
        }
    }

    @Override
    public boolean queryIfIsBookmarked() {
        if (id == 0 || type == null) {
            view.showLoadingError();
            return false;
        }

        List<HistoryEntity> query = daoSession.getHistoryEntityDao()
                .queryBuilder()
                .where(HistoryEntityDao.Properties.ContentId.eq(id),
                        HistoryEntityDao.Properties.Type.eq(type.name()),
                        HistoryEntityDao.Properties.Bookmark.eq(1))
                .list();

        if(query.size()>0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void requestData() {
        if (id == 0 || type == null) {
            view.showLoadingError();
            return;
        }

        view.showLoading();
        view.setTitle(title);
        view.showCover(coverUrl);

        // set the web view whether to show the image
        view.setImageMode(sp.getBoolean("no_picture_mode", false));

        switch (type) {
            case TYPE_ZHIHU:
                if (NetworkState.networkConnected(context)) {
                    httpMethods.loadZhihuDailyDetail(Api.ZHIHU_NEWS + id, new Subscriber<ZhihuDailyStory>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.stopLoading();
                            view.showLoadingError();
                        }

                        @Override
                        public void onNext(ZhihuDailyStory zhihuDailyStory) {
                            if (zhihuDailyStory.getBody() == null) {
                                view.showResultWithoutBody(zhihuDailyStory.getShare_url());
                            } else {
                                view.showResult(convertZhihuContent(zhihuDailyStory.getBody()));
                            }
                            view.stopLoading();
                        }

                    });
                } else {
                    HistoryEntity entity = historyEntityDao.queryBuilder()
                            .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(type.name()))
                            .build()
                            .unique();
                    if (entity != null&&entity.getContent()!=null) {
                        try {
                            zhihuDailyStory = gson.fromJson(entity.getContent(), ZhihuDailyStory.class);
                        } catch (JsonSyntaxException e) {
                            view.showResult(entity.getContent());
                        }
                        if(zhihuDailyStory!=null) {
                            view.showResult(convertZhihuContent(zhihuDailyStory.getBody()));
                        }
                    }
                }
                break;

            case TYPE_GUOKR:
                if (NetworkState.networkConnected(context)) {
                    httpMethods.loadStringRequest(Api.GUOKR_ARTICLE_LINK_V1 + id,
                            new Subscriber<ResponseBody>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    view.stopLoading();
                                    view.showLoadingError();
                                }

                                @Override
                                public void onNext(ResponseBody response) {
                                    try {
                                        convertGuokrContent(new String(response.bytes()));
                                        view.showResult(guokrStory);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        view.stopLoading();
                                        view.showLoadingError();
                                    }
                                }
                            });
                } else {
                    HistoryEntity entity = historyEntityDao.queryBuilder()
                            .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(type.name())).build().unique();
                    if (entity != null&&entity.getContent()!=null) {
                        guokrStory = entity.getContent();
                        convertGuokrContent(guokrStory);
                        view.showResult(guokrStory);
                    }
                }
                break;

            case TYPE_DOUBAN:
                if (NetworkState.networkConnected(context)) {
                    httpMethods.loadDoubanMomentDetail(Api.DOUBAN_ARTICLE_DETAIL + id, new Subscriber<DoubanMomentStory>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            view.showLoadingError();
                        }

                        @Override
                        public void onNext(DoubanMomentStory story) {
                            doubanMomentStory = story;
                            view.showResult(convertDoubanContent());
                        }

                    });
                } else {
                    HistoryEntity entity = historyEntityDao.queryBuilder()
                            .where(HistoryEntityDao.Properties.ContentId.eq(id), HistoryEntityDao.Properties.Type.eq(type.name())).build().unique();
                    if (entity != null&&entity.getContent()!=null) {
                        doubanMomentStory = gson.fromJson(entity.getContent(), DoubanMomentStory.class);
                        view.showResult(convertDoubanContent());
                    }
                }
                break;
            default:
                view.stopLoading();
                view.showLoadingError();
                break;
        }

        view.stopLoading();

    }

    @Override
    public void start() {

    }

    private String convertDoubanContent() {

        if (doubanMomentStory.getContent() == null) {
            return null;
        }
        String css;
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES) {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/douban_dark.css\" type=\"text/css\">";
        } else {
            css = "<link rel=\"stylesheet\" href=\"file:///android_asset/douban_light.css\" type=\"text/css\">";
        }
        String content = doubanMomentStory.getContent();
        ArrayList<DoubanMomentNews.posts.thumbs> imageList = doubanMomentStory.getPhotos();
        for (int i = 0; i < imageList.size(); i++) {
            String old = "<img id=\"" + imageList.get(i).getTag_name() + "\" />";
            String newStr = "<img id=\"" + imageList.get(i).getTag_name() + "\" "
                    + "src=\"" + imageList.get(i).getMedium().getUrl() + "\"/>";
            content = content.replace(old, newStr);
        }
        StringBuilder builder = new StringBuilder();
        builder.append( "<!DOCTYPE html>\n");
        builder.append("<html lang=\"ZH-CN\" xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        builder.append("<head>\n<meta charset=\"utf-8\" />\n");
        builder.append(css);
        builder.append("\n</head>\n<body>\n");
        builder.append("<div class=\"container bs-docs-container\">\n");
        builder.append("<div class=\"post-container\">\n");
        builder.append(content);
        builder.append("</div>\n</div>\n</body>\n</html>");

        return builder.toString();
    }

    private String convertZhihuContent(String preResult) {

        preResult = preResult.replace("<div class=\"img-place-holder\">", "");
        preResult = preResult.replace("<div class=\"headline\">", "");

        // 在api中，css的地址是以一个数组的形式给出，这里需要设置
        // in fact,in api,css addresses are given as an array
        // api中还有js的部分，这里不再解析js
        // javascript is included,but here I don't use it
        // 不再选择加载网络css，而是加载本地assets文件夹中的css
        // use the css file from local assets folder,not from network
        String css = "<link rel=\"stylesheet\" href=\"file:///android_asset/zhihu_daily.css\" type=\"text/css\">";


        // 根据主题的不同确定不同的加载内容
        // load content judging by different theme
        String theme = "<body className=\"\" onload=\"onLoaded()\">";
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES){
            theme = "<body className=\"\" onload=\"onLoaded()\" class=\"night\">";
        }

        return new StringBuilder()
                .append("<!DOCTYPE html>\n")
                .append("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">\n")
                .append("<head>\n")
                .append("\t<meta charset=\"utf-8\" />")
                .append(css)
                .append("\n</head>\n")
                .append(theme)
                .append(preResult)
                .append("</body></html>").toString();
    }

    private void convertGuokrContent(String content) {
        // 简单粗暴的去掉下载的div部分
        this.guokrStory = content.replace("<div class=\"down\" id=\"down-footer\">\n" +
                "        <img src=\"http://static.guokr.com/apps/handpick/images/c324536d.jingxuan-logo.png\" class=\"jingxuan-img\">\n" +
                "        <p class=\"jingxuan-txt\">\n" +
                "            <span class=\"jingxuan-title\">果壳精选</span>\n" +
                "            <span class=\"jingxuan-label\">早晚给你好看</span>\n" +
                "        </p>\n" +
                "        <a href=\"\" class=\"app-down\" id=\"app-down-footer\">下载</a>\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"down-pc\" id=\"down-pc\">\n" +
                "        <img src=\"http://static.guokr.com/apps/handpick/images/c324536d.jingxuan-logo.png\" class=\"jingxuan-img\">\n" +
                "        <p class=\"jingxuan-txt\">\n" +
                "            <span class=\"jingxuan-title\">果壳精选</span>\n" +
                "            <span class=\"jingxuan-label\">早晚给你好看</span>\n" +
                "        </p>\n" +
                "        <a href=\"http://www.guokr.com/mobile/\" class=\"app-down\">下载</a>\n" +
                "    </div>", "");

        // 替换css文件为本地文件
        guokrStory = guokrStory.replace("<link rel=\"stylesheet\" href=\"http://static.guokr.com/apps/handpick/styles/d48b771f.article.css\" />",
                "<link rel=\"stylesheet\" href=\"file:///android_asset/guokr.article.css\" />");

        // 替换js文件为本地文件
        guokrStory = guokrStory.replace("<script src=\"http://static.guokr.com/apps/handpick/scripts/9c661fc7.base.js\"></script>",
                "<script src=\"file:///android_asset/guokr.base.js\"></script>");
        if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES){
            guokrStory = guokrStory.replace("<div class=\"article\" id=\"contentMain\">",
                    "<div class=\"article \" id=\"contentMain\" style=\"background-color:#212b30; color:#878787\">");
        }
    }

    private boolean checkNull() {
        return (type == BeanType.TYPE_ZHIHU && zhihuDailyStory == null)
                || (type == BeanType.TYPE_GUOKR && guokrStory == null)
                || (type == BeanType.TYPE_DOUBAN && doubanMomentStory == null);
    }

}
