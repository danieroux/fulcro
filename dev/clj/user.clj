(ns clj.user
  (:require [clojure.test :refer (is deftest run-tests testing do-report)]
            [smooth-spec.report :as report]
            leiningen.i18n-spec
            untangled.i18n.util-spec))

(defn run-all-tests []
  (report/with-smooth-output (run-tests 'leiningen.i18n-spec))
  ;(report/with-smooth-output (run-tests 'untangled.i18n.util-spec))
  )

(run-all-tests)