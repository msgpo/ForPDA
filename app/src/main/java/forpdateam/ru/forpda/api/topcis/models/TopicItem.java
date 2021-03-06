package forpdateam.ru.forpda.api.topcis.models;

/**
 * Created by radiationx on 01.03.17.
 */

public class TopicItem {
    private boolean pinned = false, announce = false, forum = false;
    private boolean isNew = false, isPoll = false, isClosed = false;
    private int id = 0, authorId = 0, lastUserId = 0, curatorId = 0;
    private String title, desc, authorNick, lastUserNick, date, curatorNick, announceUrl;

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isAnnounce() {
        return announce;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getLastUserId() {
        return lastUserId;
    }

    public void setLastUserId(int lastUserId) {
        this.lastUserId = lastUserId;
    }

    public int getCuratorId() {
        return curatorId;
    }

    public void setCuratorId(int curatorId) {
        this.curatorId = curatorId;
    }

    /*public int getParams() {
        return params;
    }

    public void setParams(int params) {
        this.params = params;
    }*/

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isPoll() {
        return isPoll;
    }

    public void setPoll(boolean poll) {
        isPoll = poll;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthorNick() {
        return authorNick;
    }

    public void setAuthorNick(String authorNick) {
        this.authorNick = authorNick;
    }

    public String getLastUserNick() {
        return lastUserNick;
    }

    public void setLastUserNick(String lastUserNick) {
        this.lastUserNick = lastUserNick;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCuratorNick() {
        return curatorNick;
    }

    public void setCuratorNick(String curatorNick) {
        this.curatorNick = curatorNick;
    }

    public String getAnnounceUrl() {
        return announceUrl;
    }

    public void setAnnounceUrl(String announceUrl) {
        this.announceUrl = announceUrl;
    }

    public boolean isForum() {
        return forum;
    }

    public void setForum(boolean forum) {
        this.forum = forum;
    }
}
