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
package com.github.adamantcheese.chan.core.site.sites;

import com.github.adamantcheese.chan.core.model.orm.Board;
import com.github.adamantcheese.chan.core.model.orm.Loadable;
import com.github.adamantcheese.chan.core.net.NetUtils;
import com.github.adamantcheese.chan.core.site.SiteIcon;
import com.github.adamantcheese.chan.core.site.common.CommonSite;
import com.github.adamantcheese.chan.core.site.common.vichan.VichanActions;
import com.github.adamantcheese.chan.core.site.common.vichan.VichanApi;
import com.github.adamantcheese.chan.core.site.common.vichan.VichanCommentParser;
import com.github.adamantcheese.chan.core.site.common.vichan.VichanEndpoints;
import com.github.adamantcheese.chan.core.model.Post;

import java.util.Map;

import okhttp3.HttpUrl;

public class Chan1500
        extends CommonSite {
    private static final HttpUrl ROOT = HttpUrl.get("https://1500chan.org/");
    public static final CommonSiteUrlHandler URL_HANDLER = new CommonSiteUrlHandler() {
        @Override
        public HttpUrl getUrl() {
            return ROOT;
        }

        @Override
        public String desktopUrl(Loadable loadable, int postNo) {
            if (loadable.isCatalogMode()) {
                return getUrl().newBuilder().addPathSegment(loadable.boardCode).toString();
            } else if (loadable.isThreadMode()) {
                return getUrl().newBuilder()
                        .addPathSegment(loadable.boardCode)
                        .addPathSegment("res")
                        .addPathSegment(loadable.no + ".html")
                        .toString();
            } else {
                return getUrl().toString();
            }
        }
    };

    public Chan1500() {
        setName("1500chan");
        setIcon(SiteIcon.fromFavicon(HttpUrl.parse("https://1500chan.org/static/favicon.ico")));
    }

    @Override
    public void setup() {
        setBoards(
                Board.fromSiteNameCode(this, "Random", "b"),
                Board.fromSiteNameCode(this, "Bairrismo", "bairro"),
                Board.fromSiteNameCode(this, "Copi-Cola", "cc"),
                Board.fromSiteNameCode(this, "Criptomoedas e Câmbio", "c"),
                Board.fromSiteNameCode(this, "Finanças", "$"),
                Board.fromSiteNameCode(this, "Jogatina", "jo"),
                Board.fromSiteNameCode(this, "Jogatina Conjunta", "lan"),
                Board.fromSiteNameCode(this, "RPG e Jogos de Tabuleiro", "mesa"),
                Board.fromSiteNameCode(this, "Política", "pol"),
                Board.fromSiteNameCode(this, "Religião", "sancti"),
                Board.fromSiteNameCode(this, "Programação e Computação", "pc"),
                Board.fromSiteNameCode(this, "Criatividade e Arte", "cri"),
                Board.fromSiteNameCode(this, "Política", "pol"),
                Board.fromSiteNameCode(this, "Armas e Militaria", "arm"),
                Board.fromSiteNameCode(this, "Falha e Aleatoriedade", "mago"),
                Board.fromSiteNameCode(this, "DIY", "fvm"),
                Board.fromSiteNameCode(this, "Veículos e Transportes", "ve"),
                Board.fromSiteNameCode(this, "Culinária", "coz"),
                Board.fromSiteNameCode(this, "Natureza", "nat"),
                Board.fromSiteNameCode(this, "Ocultismo e Paranormal", "x"),
                Board.fromSiteNameCode(this, "Japão e Cultura Otaku", "jp"),
                Board.fromSiteNameCode(this, "Quadrinhos Ocidentais (Comics)", "hq"),
                Board.fromSiteNameCode(this, "Música", "mu"),
                Board.fromSiteNameCode(this, "Televisão e Cinema", "tvc"),
                Board.fromSiteNameCode(this, "Futebol e outros esportes", "esp"),
                Board.fromSiteNameCode(this, "Direito e Estudos Jurídicos", "jus"),
                Board.fromSiteNameCode(this, "Estudos de Idiomas", "lang"),
                Board.fromSiteNameCode(this, "Literatura", "lit"),
                Board.fromSiteNameCode(this, "Universidade Federal da Caravela", "UFC"),
                Board.fromSiteNameCode(this, "Self Improvement General", "sig"),
                Board.fromSiteNameCode(this, "Fitness", "fit"),
                Board.fromSiteNameCode(this, "Moda", "clô"),
                Board.fromSiteNameCode(this, "Medicina e Drogas", "med"),
                Board.fromSiteNameCode(this, "Depressão", "muié"),
                Board.fromSiteNameCode(this, "*fapfapfap*", "pron"),
                Board.fromSiteNameCode(this, "Neomulheres", "tr"),
                Board.fromSiteNameCode(this, "Arquivo", "arquivo")
        );

        setResolvable(URL_HANDLER);

        setConfig(new CommonConfig() {
            @Override
            public boolean siteFeature(SiteFeature siteFeature) {
                return super.siteFeature(siteFeature) || siteFeature == SiteFeature.POSTING;
            }
        });

        setEndpoints(new VichanEndpoints(this, "https://1500chan.org", "https://1500chan.org") {
            @Override
            public HttpUrl imageUrl(Post.Builder post, Map<String, String> arg) {
                return HttpUrl.parse("https://1500chan.org/" + post.board.code + "/src/" + arg.get("tim") + "." + arg.get("ext"));
            }

            @Override
            public HttpUrl thumbnailUrl(Post.Builder post, boolean spoiler, Map<String, String> arg) {
                String ext;
                switch (arg.get("ext")) {
                    case "jpeg":
                    case "jpg":
                    case "png":
                    case "gif":
                        ext = arg.get("ext");
                        break;
                    default:
                        ext = "jpg";
                        break;
                }
                // this is imperfect, for some reason some thumbnails are png and others are jpg randomly
                // kinda mucks up the image viewing as well
                return HttpUrl.parse("https://1500chan.org/" + post.board.code + "/thumb/" + arg.get("tim") + "." + ext);
            }
        });

        setActions(new VichanActions(this) {
            @Override
            public void clearCookies() {
                NetUtils.clearCookies(ROOT);
            }
        });
        setApi(new VichanApi(this));
        setParser(new VichanCommentParser());
    }
}
