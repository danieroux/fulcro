(ns com.fulcrologic.fulcro.cards.console-errors-display
  "A not-React component that shows console.error's out-of-band.

  Shift-Click makes it go away"
  (:require
   [goog.dom :as gdom]))

(def !tolerated-errors
  (atom
    #{#"^WebSocket"
      #"com.fulcrologic.devtools.common.connection"
      ; This needs to go away?
      #"ReactDOM.render is no longer supported in React 18"}))

(defn inject-css!
  []
  (let [style (gdom/createDom "style" #js {:type "text/css"})
        css   "
        #errorsnitchershell {
          visibility: hidden;
          width: 99%; height: 99%;
          margin: 0;
          padding: 1em;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          background-color: #f44336;
          border-radius: 10px;
          position: fixed;
          z-index: 131313;
          cursor: pointer;
        }

        #errorsnitchershell.show {
          visibility: visible;
          opacity: 0.60;
        }

        #errorsnitcher {
          padding: 1em;
          color: white;
          background-color: black;
          font-size: 17px;
          font-weight: bold;
          text-align: left;
          opacity: 1.00;
        }

        #errorsnitcherClose {
          text-align: center;
          padding: 5px;
          font-size: 20px;
          color: white;
          background-color: black;
          opacity: 1.00;
        }"]
    (set! (.-textContent style) css)
    (.appendChild (.-head js/document) style)))

(defn create-error-snitcher!
  []
  (let [errorsnitchershell (gdom/createDom "div" #js {:id "errorsnitchershell"})
        messagebox         (gdom/createDom "div" #js {:id "errorsnitcherClose"})
        errorsnitcher      (gdom/createDom "div" #js {:id "errorsnitcher"})]
    (set! (.-textContent messagebox) "A console.error got logged. See console for more details - Shift-Click to Close")
    (.appendChild errorsnitchershell messagebox)
    (.appendChild errorsnitchershell errorsnitcher)
    (.appendChild (.-body js/document) errorsnitchershell)
    (.addEventListener errorsnitchershell "click"
      (fn [event]
        (when (.-shiftKey event)
          (set! (.-className errorsnitchershell) ""))))
    errorsnitchershell))

(defn show-errorsnitcher
  [message]
  (let [errorsnitchershell (gdom/getElement "errorsnitchershell")
        errorsnitcher      (gdom/getElement "errorsnitcher")]
    (set! (.-textContent errorsnitcher) message)
    (set! (.-className errorsnitchershell) "show")))

(defn matches-tolerated-error? [message]
  (some #(re-find % message) @!tolerated-errors))

(defn override-console-error! []
  (let [original-console-error js/console.error]
    (set! js/console.error
      (fn [& args]
        (apply original-console-error args)
        (let [message (clojure.string/join " " args)]
          (when-not (matches-tolerated-error? message)
            (show-errorsnitcher (clojure.string/join " " args))))))))

(def !initialised (atom nil))
(defn init!
  [add-to-tolerated-errors]
  (swap! !tolerated-errors into add-to-tolerated-errors)
  (when-not @!initialised
    (inject-css!)
    (create-error-snitcher!)
    (override-console-error!)
    (reset! !initialised true)))

(comment
  (init! #{})

  (js/console.error "Woops!")
  ())
