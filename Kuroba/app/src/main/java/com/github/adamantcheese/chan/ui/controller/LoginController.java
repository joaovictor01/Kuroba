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
package com.github.adamantcheese.chan.ui.controller;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.github.adamantcheese.chan.R;
import com.github.adamantcheese.chan.controller.Controller;
import com.github.adamantcheese.chan.core.net.NetUtilsClasses;
import com.github.adamantcheese.chan.core.site.Site;
import com.github.adamantcheese.chan.core.site.http.LoginRequest;
import com.github.adamantcheese.chan.core.site.http.LoginResponse;
import com.github.adamantcheese.chan.ui.view.CrossfadeView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.adamantcheese.chan.utils.AndroidUtils.getString;
import static com.github.adamantcheese.chan.utils.AndroidUtils.hideKeyboard;

public class LoginController
        extends Controller
        implements NetUtilsClasses.ResponseResult<LoginResponse> {
    private CrossfadeView crossfadeView;
    private TextView errors;
    private Button button;
    private EditText inputToken;
    private EditText inputPin;
    private TextView authenticated;

    private final Site site;

    public LoginController(Context context, Site site) {
        super(context);
        this.site = site;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        navigation.setTitle(R.string.settings_screen_pass);

        view = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.controller_pass, null);
        crossfadeView = view.findViewById(R.id.crossfade);
        errors = view.findViewById(R.id.errors);
        button = view.findViewById(R.id.button);
        TextView bottomDescription = view.findViewById(R.id.bottom_description);
        inputToken = view.findViewById(R.id.input_token);
        inputPin = view.findViewById(R.id.input_pin);
        authenticated = view.findViewById(R.id.authenticated);

        errors.setVisibility(GONE);

        final boolean loggedIn = site.actions().isLoggedIn();
        if (loggedIn) {
            button.setText(R.string.setting_pass_logout);
        }
        button.setOnClickListener((v) -> {
            if (site.actions().isLoggedIn()) {
                site.actions().logout();
                crossfadeView.toggle(true, true);
                button.setText(R.string.submit);
            } else {
                hideKeyboard(view);
                inputToken.setEnabled(false);
                inputPin.setEnabled(false);
                button.setEnabled(false);
                button.setText(R.string.loading);

                String user = inputToken.getText().toString();
                String pass = inputPin.getText().toString();
                site.actions().login(new LoginRequest(site, user, pass), this);
            }

            errors.setText(null);
            errors.setVisibility(GONE);
        });

        bottomDescription.setText(HtmlCompat.fromHtml(getString(R.string.setting_pass_bottom_description),
                HtmlCompat.FROM_HTML_MODE_LEGACY
        ));
        bottomDescription.setMovementMethod(LinkMovementMethod.getInstance());

        LoginRequest loginDetails = site.actions().getLoginDetails();
        inputToken.setText(loginDetails.user);
        inputPin.setText(loginDetails.pass);

        // Sanity check
        if (parentController.view.getWindowToken() == null) {
            throw new IllegalArgumentException("parentController.view not attached");
        }

        crossfadeView.toggle(!loggedIn, false);
    }

    @Override
    public void onSuccess(LoginResponse loginResponse) {
        if (loginResponse.isSuccess()) {
            crossfadeView.toggle(false, true);
            button.setText(R.string.setting_pass_logout);
            authenticated.setText(loginResponse.getMessage());
        } else {
            authFail(loginResponse);
        }

        authAfter();
    }

    @Override
    public void onFailure(Exception e) {
        authFail(new LoginResponse(getString(R.string.setting_pass_error), false));
        authAfter();
    }

    private void authFail(LoginResponse response) {
        errors.setText(response.getMessage());
        errors.setVisibility(VISIBLE);
        button.setText(R.string.submit);
    }

    private void authAfter() {
        button.setEnabled(true);
        inputToken.setEnabled(true);
        inputPin.setEnabled(true);
    }
}
