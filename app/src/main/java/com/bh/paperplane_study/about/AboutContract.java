/*
 * Copyright 2017 lizhaotailang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bh.paperplane_study.about;

import com.bh.paperplane_study.BasePresenter;
import com.bh.paperplane_study.BaseView;

/**
 * Created by Lizhaotailang on 2016/9/4.
 * This specifies the contract between the view and the presenter.
 */

public interface AboutContract {

    interface View extends BaseView<Presenter> {

        void showFeedbackError();

        void showBrowserNotFoundError();

    }

    interface Presenter extends BasePresenter {

        void openLicense();

        void followOnGithub();

        void feedback();
    }

}
