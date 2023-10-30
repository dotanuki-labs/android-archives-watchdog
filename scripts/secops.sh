#! /usr/bin/env bash

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly github_token="$1"
readonly git_commit="$2"
readonly sbom="aaw-sbom-$git_commit.json"

install_bomber() {
    local package="bomber_0.4.5_linux_amd64.tar.gz"
    local download_url="https://github.com/devops-kung-fu/bomber/releases/download/v0.4.5/$package"
    curl -fsSL -o "$package" -C - "$download_url"
    tar -xzvf "$package"
}

download_sbom() {
    local download_url="https://api.github.com/repos/dotanuki-labs/android-archives-watchdog/dependency-graph/sbom"
    curl -fsSL \
        -H "Accept: application/vnd.github+json" \
        -H "Authorization: Bearer $github_token" \
        -H "X-GitHub-Api-Version: 2022-11-28" \
        -o "$sbom" \
        "$download_url"
}

analyse_sbom() {
    ./bomber scan .
}

echo
echo "🔥 Analysing CVEs attached to the latest Gradle dependency graph"
echo

install_bomber
download_sbom
analyse_sbom

echo
echo "✅ Done"
echo
