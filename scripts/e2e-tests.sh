#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

e2e() {
    local repo="$1"
    local version="$2"
    local artifact="$3"
    local baseline="$4"
    local download_url="https://github.com/$repo/releases/download/$version/$artifact"

    echo
    echo -e "• Downloading $download_url"
    rm -rf ".tmp/$artifact"
    curl -fsSL -o ".tmp/$artifact" -C - "$download_url"

    echo -e "• Comparing against $baseline"

    aaw compare -a ".tmp/$artifact" -b "$baseline" >/dev/null

    echo -e "✔ No issues found"
}

test_duckduckgo() {
    local repo="duckduckgo/Android"
    local version="5.175.1"
    local artifact="duckduckgo-$version-play-release.apk"
    local baseline="scripts/baselines/duckduckgo-$version.toml"

    e2e "$repo" "$version" "$artifact" "$baseline"
}

test_woocomerce() {
    local repo="woocommerce/woocommerce-android"
    local version="15.7"
    local artifact="wcandroid-$version.aab"
    local baseline="scripts/baselines/woocommerce-$version.toml"

    e2e "$repo" "$version" "$artifact" "$baseline"
}

test_firefox() {
    # https://github.com/mozilla-mobile/firefox-android/releases/download/focus-v118.2.0/focus-118.2.0-arm64-v8a.apk
    local repo="mozilla-mobile/firefox-android"
    local version="118.2.0"
    local release_version="focus-v$version"
    local artifact="focus-$version-arm64-v8a.apk"
    local baseline="scripts/baselines/firefoxfocus-$version.toml"

    e2e "$repo" "$release_version" "$artifact" "$baseline"
}

test_protonmail() {
    local repo="ProtonMail/proton-mail-android"
    local version="3.0.17"
    local artifact="ProtonMail-$version.apk"
    local baseline="scripts/baselines/protonmail-$version.toml"

    e2e "$repo" "$version" "$artifact" "$baseline"
}

if ! which aaw >/dev/null; then
    echo "✗ Error : missing 'aaw' executable in your path"
    echo
    exit 1
fi

mkdir -p ".tmp"

echo
echo "🔥 Running E2E tests"

test_duckduckgo
test_woocomerce
test_firefox
test_protonmail

rm -rf ".tmp"

echo
echo "🔥 Success"
echo