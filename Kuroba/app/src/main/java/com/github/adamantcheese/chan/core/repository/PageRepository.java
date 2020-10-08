/*
 * Kuroba - *chan browser https://github.com/Adamantcheese/Kuroba/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.adamantcheese.chan.core.repository;

import androidx.annotation.NonNull;

import com.github.adamantcheese.chan.core.model.Post;
import com.github.adamantcheese.chan.core.model.orm.Board;
import com.github.adamantcheese.chan.core.model.orm.Loadable;
import com.github.adamantcheese.chan.core.site.common.CommonDataStructs.ChanPage;
import com.github.adamantcheese.chan.core.site.common.CommonDataStructs.ChanPages;
import com.github.adamantcheese.chan.core.site.common.CommonDataStructs.ThreadNoTimeModPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.util.concurrent.TimeUnit.MINUTES;

public class PageRepository {
    private static Set<String> requestedBoards = Collections.synchronizedSet(new HashSet<>());
    private static Set<String> savedBoards = Collections.synchronizedSet(new HashSet<>());
    private static ConcurrentMap<String, ChanPages> boardPagesMap = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, Long> boardTimeMap = new ConcurrentHashMap<>();

    private static List<PageCallback> callbackList = new ArrayList<>();

    public static ChanPage getPage(@NonNull Post op) {
        return findPage(op.board, op.no);
    }

    public static ChanPage getPage(@NonNull Loadable opLoadable) {
        return findPage(opLoadable.board, opLoadable.no);
    }

    public static void forceUpdateForBoard(Board b) {
        requestBoard(b);
    }

    private static ChanPage findPage(Board board, int opNo) {
        ChanPages pages = getPages(board);
        if (pages == null) return null;
        for (ChanPage page : pages) {
            for (ThreadNoTimeModPair threadNoTimeModPair : page.threads) {
                if (opNo == threadNoTimeModPair.no) {
                    return page;
                }
            }
        }
        return null;
    }

    private static ChanPages getPages(Board b) {
        if (savedBoards.contains(b.code)) {
            //if we have it stored already, return the pages for it
            //also issue a new request if 3 minutes have passed
            shouldUpdate(b);
            return boardPagesMap.get(b.code);
        } else {
            //otherwise, get the site for the board and request the pages for it
            requestBoard(b);
            return null;
        }
    }

    private static void shouldUpdate(Board b) {
        if (b == null) return; //if for any reason the board is null, don't do anything
        Long lastUpdate = boardTimeMap.get(b.code); //had some null issues for some reason? arisuchan in particular?
        long lastUpdateTime = lastUpdate != null ? lastUpdate : 0L;
        if (lastUpdateTime + MINUTES.toMillis(3) <= System.currentTimeMillis()) {
            requestBoard(b);
        }
    }

    private static synchronized void requestBoard(Board b) {
        if (!requestedBoards.contains(b.code)) {
            requestedBoards.add(b.code);
            b.site.actions().pages(b, (b1, pages) -> {
                savedBoards.add(b1.code);
                requestedBoards.remove(b1.code);
                boardTimeMap.put(b1.code, System.currentTimeMillis());
                boardPagesMap.put(b1.code, pages);

                for (PageCallback callback : callbackList) {
                    callback.onPagesReceived();
                }
            });
        }
    }

    public static void addListener(PageCallback callback) {
        if (callback != null) {
            callbackList.add(callback);
        }
    }

    public static void removeListener(PageCallback callback) {
        if (callback != null) {
            callbackList.remove(callback);
        }
    }

    public interface PageCallback {
        void onPagesReceived();
    }
}