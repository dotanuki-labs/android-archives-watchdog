#! /usr/bin/env bash
# Copyright 2023 Dotanuki Labs
# SPDX-License-Identifier: MIT

set -euo pipefail

dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${dir%/*}"

readonly pr_number="$1"
readonly build_scan_url="$2"

pr_body="Latest Gradle Build Scan : $build_scan_url"
gh pr comment "$pr_number" --body "$pr_body" --edit-last || gh pr comment "$pr_number" --body "$pr_body"
