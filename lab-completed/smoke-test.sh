#!/usr/bin/env bash
# Smoke test for a running lab-completed app: pages, htmx fragments, forms and SSE.
# Usage: ./smoke-test.sh [base-url]   (default http://localhost:8080)
set -euo pipefail
B="${1:-http://localhost:8080}"
HX=(-H 'HX-Request: true')

expect() { # name, needle, body
  if grep -q -- "$2" <<<"$3"; then echo "ok   $1"; else echo "FAIL $1 (missing: $2)"; exit 1; fi
}

expect "GET / layout"              'hx-get="/user-table/0"'      "$(curl -sf "$B/")"
expect "GET /htmx_1.9.11.js"       'htmx'                         "$(curl -sf "$B/htmx_1.9.11.js" | head -c 2000)"
expect "GET /css/sakura.css"       'body'                         "$(curl -sf "$B/css/sakura.css")"
table=$(curl -sf "${HX[@]}" "$B/user-table/0")
expect "HX /user-table/0"          'Thomas'                       "$table"
expect "HX /user-table/0 sse"      'sse-connect="/subscribe-new-user"' "$table"
expect "HX /user-table/1 infinite" 'hx-get="/user-table/2"'       "$(curl -sf "${HX[@]}" "$B/user-table/1")"
expect "HX /search-user hit"       'Cassandra'                    "$(curl -sf "${HX[@]}" "$B/search-user?searchQuery=Cassandra")"
expect "HX /search-user miss toast" 'HX-Retarget: #toastContainer' "$(curl -sfi "${HX[@]}" "$B/search-user?searchQuery=zz-no-such-user")"
uuid=$(grep -oE 'user-[0-9a-f-]{36}' <<<"$table" | head -1 | cut -c6-)
expect "GET /save-user/modal"      "value=\"$uuid\""              "$(curl -sf "$B/save-user/modal/$uuid")"
expect "POST /save-user"           'renamed-by-smoke'             "$(curl -sf "${HX[@]}" -d "uuid=$uuid&username=renamed-by-smoke&password=pw" "$B/save-user")"
expect "GET /create-user/modal"    'hx-post="/create-user"'       "$(curl -sf "$B/create-user/modal")"
expect "POST /create-user"         'created-by-smoke'             "$(curl -sf "${HX[@]}" -d 'username=created-by-smoke&password=pw' "$B/create-user")"
expect "GET /group-management"     'USER_GROUP'                   "$(curl -sf "$B/group-management")"
expect "GET /group/../select-user" 'Add User to group'            "$(curl -sf "$B/group/ADMIN_GROUP/select-user")"
expect "POST /group/../add-user"   'renamed-by-smoke'             "$(curl -sf -d "userId=$uuid" "$B/group/ADMIN_GROUP/add-user")"
# SSE pushes a freshly generated user row every 5s
expect "SSE /subscribe-new-user"   'data:<tr id="user-'           "$(curl -sN --max-time 7 -H 'Accept: text/event-stream' "$B/subscribe-new-user" || true)"
echo "all smoke checks passed"
