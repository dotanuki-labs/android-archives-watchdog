#! /usr/bin/env bash
# Copyright 2023 Dotanuki Labs
# SPDX-License-Identifier: MIT

# shellcheck disable=SC2086
# shellcheck disable=SC2046

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

check_licenses() {
    local files="$1"
    local image="ghcr.io/google/addlicense"
    echo "Checking licenses on $files files"
    docker run -v ${PWD}:/src "$image" -c "Dotanuki Labs" -l "mit" -check $(find . -type f -name "$files")
}

echo "Checking licenses on Kotlin files"
check_licenses "*.kt"
check_licenses "*.kts"

echo "Checking licenses on Bash files"
check_licenses "*.sh"
check_licenses "*.bats"
