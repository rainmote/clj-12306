(ns clj-12306.ui.cdn
  (:require [clj-12306.ui.base :as base]
            [antizer.reagent :as ant]
            [clj-12306.events]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn query-ip-info [table-data]
  (rf/dispatch [:request-query-ip-info [{:query "1.1.1.1"}]])
  (->> table-data
       (map #(:ip %) ,)
       (map #(-> {}
                 (assoc, :query %)
                 (assoc, :lang "zh-CN")),)
       (partition-all 100 ,)
       (map #(rf/dispatch [:request-query-ip-info %]),)))

(defn meta-form []
  (fn [props]
    (let [form (ant/get-form)
          form-style
               (fn [name]
                 {:label-col {:span 6}
                  :wrapper-col {:span 14}
                  :label name})
          get-info-on-click
               (fn [e]
                 (rf/dispatch [:set-cdn-import-form (ant/get-fields-value form)])
                 (query-ip-info @(rf/subscribe [:cdn-import-table])))]
      [ant/form
       [ant/row {:gutter 24}
        [ant/col {:span 8 :key "domain"}
         [ant/form-item (form-style "Domain")
          (ant/decorate-field form "domain" {:rules [{:require true}]}
                              [ant/input])]]

        [ant/col {:span 8 :key "tag"}
         [ant/form-item (form-style "Tag")
          (ant/decorate-field form "tag" {:rules [{:require true}]}
                              [ant/input])]]

        [ant/col {:span 8 :key "button"}
         [ant/button {:type "primary"
                      :onClick get-info-on-click}
          "补充&获取信息"]]]])))

(defn comparison [data1 data2 field]
  (compare (get (js->clj data1 :keywordize-keys true) field)
           (get (js->clj data2 :keywordize-keys true) field)))

(def cols [{:title "IP" :dataIndex "ip" :sorter #(comparison %1 %2 :ip)}
           {:title "AreaCode" :dataIndex "areacode"}
           {:title "Longitude" :dataIndex "longitude"}
           {:title "Latitude" :dataIndex "latitude"}
           {:title "Domain" :dataIndex "domain" :sorter #(comparison %1 %2 :domain)}
           {:title "Tag" :dataIndex "tag"}])

(def pagination {:show-size-changer true
                 :page-size 5
                 :page-size-options ["5" "10" "20"]
                 :show-total #(str (first %2) "-" (second %2) " of " %1 " items")})

(defn cdn-import-origin-data []
  (let [on-change
        (fn [e]
          (rf/dispatch [:set-cdn-origin-data (-> e .-target .-value)]))

        find-ip-on-click
        (fn [_]
          (let [iplist (into #{} (re-seq @(rf/subscribe [:ip-regex])
                                         @(rf/subscribe [:cdn-origin-data])))
                data   (map #(-> {} (assoc, :ip %)) iplist)]
            (rf/dispatch [:set-cdn-import-table data])))

        ip-regex-on-change
        (fn [e]
          (rf/dispatch [:set-ip-regex (-> e .-target .-value)]))]
    [:div
     [ant/input-text-area {:rows 6
                           :defaultValue @(rf/subscribe [:cdn-origin-data])
                           :on-change on-change}]
     [:p]
     [ant/input {:addonBefore "IP正则表达式"
                 :defaultValue @(rf/subscribe [:ip-regex])
                 :onChange ip-regex-on-change}]

     [:div {:style {:padding-top "10px"}}
      [ant/button {:type "primary"
                   :onClick find-ip-on-click}
       "查找IP"]]]))

(defn cdn-import-data-table []
  (let [data  @(rf/subscribe [:cdn-import-table])
        error @(rf/subscribe [:cdn-import-error])]
    (when error
      (ant/alert {:type "error"
                  :message "Error"
                  :showIcon true
                  :description (str error)}))
    [ant/table {:columns cols
                :dataSource data
                :pagination pagination
                :row-key "ip"}]))

(defn cdn-import-data-page []
  [:div
   [ant/divider {:orientation "left"} "请输入原始数据"]
   (cdn-import-origin-data)

   [:div {:style {:padding-top "30px"}}
    [ant/divider {:orientation "left"} "元信息"]
    [ant/create-form (meta-form)]]

   [:div {:style {:padding-top "30px"}}
    [ant/divider {:orientation "left"} "结果"]
    (cdn-import-data-table)
    ]])

(defn cdn-query-page []
  [:div {} "cdn query page"])
