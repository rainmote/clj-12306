(ns clj-12306.ui.base
  (:require [antizer.reagent :as ant]
            [clj-12306.events]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(def menu-data
  [{:type :menu
    :nav-path [:home-page]
    :ns "clj-12306.ui.home"
    :icon "home"
    :name "首页"}
   {:type :menu
    :nav-path [:home-page :create-task-page]
    :ns "clj-12306.ui.task"
    :icon "form"
    :name "新建抢票任务"}
   {:type :menu
    :nav-path [:home-page :query-task-page]
    :ns "clj-12306.ui.task"
    :icon "search"
    :name "任务查询"}
   {:type :submenu
    :nav-path [:home-page :cdn-manager]
    :title {:type :menu
            :icon "setting"
            :name "CDN管理"}
    :data [{:type :menu
            :nav-path [:home-page :cdn-manager :cdn-import-data-page]
            :ns "clj-12306.ui.cdn"
            :name "导入数据"}
           {:type :menu
            :nav-path [:home-page :cdn-manager :cdn-query-page]
            :ns "clj-12306.ui.cdn"
            :name "查询"}]}])

(defn header []
  [ant/layout-header {:style {:background "#FFF" :padding 0 :height 0}}])

(defn footer []
  [ant/layout-footer {:style {:textAlign "center"}}
   "clj-12306 @2018 by rainmote."])

(defn menu-item-data [icon name]
  (if icon
    (r/as-element [:span [ant/icon {:type icon}] name])
    name))

(defn generate-menu-view [m]
  (for [it m]
    (condp = (:type it)
      :menu
      [ant/menu-item {:key (name (last (:nav-path it)))}
       (menu-item-data (some-> it :icon) (some-> it :name))]

      :submenu
      [ant/menu-sub-menu {:key (name (last (:nav-path it)))
                          :title (menu-item-data (some-> it :title :icon)
                                                 (some-> it :title :name))}
       (generate-menu-view (:data it))]

      (.log js/console "Parse menu data error, " (str it)))))

(defn link->name [data link]
  (for [it data]
    (condp = (:type it)
      :menu
      (if (= (:link it) link)
        (:name it))

      :submenu
      (if (= (:link it) link)
        (-> it :title :name)
        (link->name (:data it) link)))))

(defn content [nav-path user-data]
  [ant/layout-content {:style {:margin "0 16px"}}
   [ant/breadcrumb {:style {:margin "16px 0"}}
    (for [link nav-path]
      [ant/breadcrumb-item {:key link}
       (or (link->name menu-data link) "unknow")])]
   [:div {:style {:minHeight 360 :padding 24 :background "#FFF"}}
    user-data]])

(defn parse-nav [data page]
  (for [it data]
    (condp = (:type it)
      :menu
      (if (= (last (:nav-path it)) page)
        (:nav-path it))

      :submenu
      (parse-nav (:data it) page))))

(defn switch-page [e]
  (rf/dispatch (into [] (cons :navigate
                             (->> (parse-nav menu-data
                                             (keyword (get (js->clj e) "key")))
                                  flatten
                                  (filter #(not (nil? %))))))))

(defn sider []
  [ant/layout-sider {:collapsible true
                     :collapsed false}
   [:h1 {:align "center"
         :style {:color "#4682B4" :padding-top 20}}
    "Rain mote"]
   [ant/menu {:theme "dark"
              :mode "inline"
              :onClick switch-page}
    (generate-menu-view menu-data)]])

