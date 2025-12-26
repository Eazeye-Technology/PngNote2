package com.txkj.notemobile2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.txkj.notemobile2.book.BookIO;
import com.txkj.notemobile2.book.BookPage;
import com.txkj.notemobile2.book.FastFile;

public class Book {
    private FastFile bookDir;
    private List<FastFile> pages;
    private FastFile bgImage;

    public Book(FastFile bookDir, List<FastFile> pages, FastFile bgImage) {
        this.bookDir = bookDir;
        this.pages = pages;
        this.bgImage = bgImage;
        if (pages != null) {
            for (int i = 0; i < pages.size(); ++i) {
                FastFile fastFile = pages.get(i);
                if (fastFile != null) {
                    String name = fastFile.getName();
                    if (name != null && name.endsWith(".png")) {
                        pageNameMap.put(i, name.substring(0, name.length() - ".png".length()));
                    }
                }
            }
        }
    }

    public FastFile getBookDir() {
        return this.bookDir;
    }
    public List<FastFile> getPages() {
        return this.pages;
    }
    public FastFile getBgImage() {
        return this.bgImage;
    }

    public Book addPage() {
        FastFile pngFile = BookPage.createEmptyFile(this.bookDir, this.pages.size(), this);
        List<FastFile> result = new ArrayList<FastFile>(this.pages);
        result.add(pngFile);
        return new Book(this.bookDir, result, this.bgImage);
    }

    public void reorderPage(List<FastFile> pages_, BookIO bookIO) {
        if (pages_ != null) {
            this.pages.clear();
            this.pages.addAll(pages_);
            //rebuild pageNameMap
            {
                this.pageNameMap.clear();
                for (int i = 0; i < this.pages.size(); ++i) {
                    FastFile fastFile = this.pages.get(i);
                    if (fastFile != null) {
                        String name = fastFile.getName();
                        if (name != null && name.endsWith(".png")) {
                            this.pageNameMap.put(i, name.substring(0, name.length() - ".png".length()));
                        }
                    }
                }
            }
        }
    }

    //FIXME:???
    public void removePage(FastFile page_, BookIO bookIO) {
        if (page_ != null) {
            this.pages.remove(page_);
            //rebuild pageNameMap
            {
                this.pageNameMap.clear();
                for (int i = 0; i < this.pages.size(); ++i) {
                    FastFile fastFile = this.pages.get(i);
                    if (fastFile != null) {
                        String name = fastFile.getName();
                        if (name != null && name.endsWith(".png")) {
                            this.pageNameMap.put(i, name.substring(0, name.length() - ".png".length()));
                        }
                    }
                }
            }
            page_.removeBookFile(bookIO, this);
        }
    }

    public BookPage getPage(int idx) {
        return new BookPage(this.pages.get(idx), idx);
    }

    //FIXME:????
    public Book assignNonEmpty(int pageIdx) {
        BookPage page = this.getPage(pageIdx);
        if (page != null && page.getFile() != null && !page.getFile().isEmpty()) {
            return this;
        }

        List<FastFile> it = new ArrayList<FastFile>();
        try {
            for (int idx = 0; idx < this.pages.size(); ++idx) {
                FastFile file = this.pages.get(idx);
                if (idx != pageIdx) {
                    it.add(file);
                } else {
                    try {
                        it.add(FastFile.copy(file, null, null, null, 0L,
                                null, 1000L, null, 47, null));
                    } catch (Throwable eee) {
                        eee.printStackTrace();
                    }
                }
            }
        } catch (Throwable eee2) {
            eee2.printStackTrace();
        }
        return new Book(this.bookDir, it, this.bgImage);
    }

    public String getName() {
        return this.bookDir.getName();
    }

    //--------------------
    //new api

    private Map<Integer, String> pageNameMap = new HashMap<Integer, String>();
    public void newPageName(int index, String name) {
        pageNameMap.put(index, name);
    }
    public Map<Integer, String> getPagetNameMap() {
        return pageNameMap;
    }
}
