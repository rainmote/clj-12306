(ns clj-12306.core
  (:require [baking-soda.core :as b]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]]
            [secretary.core :as secretary]
            [antizer.reagent :as ant]
            [clj-12306.ajax :as ajax]
            [clj-12306.events]
            [clj-12306.ui.base :as ui]
            [clj-12306.ui.home :as home]
            [clj-12306.ui.cdn :as cdn]
            [clj-12306.ui.task :as task])
  (:import goog.History))

(defn router [x]
  (get {:home-page #'home/home-page
        :create-task-page #'task/create-task-page
        :query-task-page #'task/query-task-page
        :cdn-import-data-page #'cdn/cdn-import-data-page
        :cdn-query-page #'cdn/cdn-query-page}
       x))

(defn gen-page []
  (let [nav-path @(rf/subscribe [:nav-path])]
    [ant/layout {:style {:height "100%"}}
     (ui/sider)
     [ant/layout
      (ui/header)
      (ui/content nav-path ((router (last nav-path))))
      (ui/footer)]]))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (rf/dispatch [:navigate :home-page]))

(secretary/defroute "/page/:name" [name]
                    (rf/dispatch (->> (clojure.string/split name #"/")
                                      (map keyword)
                                      (cons :home-page)
                                      vector)))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'gen-page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initdb])
  (rf/dispatch-sync [:navigate :home-page])
  (ajax/load-interceptors!)
  (hook-browser-navigation!)
  (mount-components))
