# Shopping List — build the APK with no local tools

This folder is a complete Android Studio project. You don't need Android
Studio or the Android SDK installed anywhere — GitHub's own servers will
compile it for you.

## 1. Create a new repository
Go to https://github.com/new, give it any name (e.g. `shopping-list-app`),
keep it **Public or Private** (either works), and click **Create repository**.
Do NOT initialize it with a README — leave it empty.

## 2. Upload these files
On the new repo's page, click **"uploading an existing file"**.
Drag the entire contents of this folder (including the hidden `.github`
folder — see note below) into the browser upload box, then click
**Commit changes**.

> The `.github/workflows/build-apk.yml` file is what tells GitHub to build
> the APK. GitHub's drag-and-drop uploader does accept folders that start
> with a dot, but if your browser hides dotfiles, use "Add file" →
> "Create new file", type the path
> `.github/workflows/build-apk.yml` as the filename, and paste in the
> contents of that file instead.

## 3. Let GitHub build it
As soon as the commit lands, go to the **Actions** tab of your repo.
You'll see a workflow run called "Build APK" start automatically
(takes about 3–5 minutes).

## 4. Download the APK
Once the run finishes with a green check:
- Easiest: go to the **Releases** section (right sidebar of the repo, or
  `github.com/<you>/<repo>/releases`) and download `app-debug.apk` directly.
- Alternative: open the finished Actions run → scroll to **Artifacts** →
  download `shopping-list-debug-apk` (this comes as a `.zip` containing
  the apk).

## 5. Install on your phone
Transfer `app-debug.apk` to your Android phone (email, Drive, USB — any
way you like), tap it, allow "install from this source" if asked, and
it installs like any app.

## What's implemented
- Multiple shopping lists, created from the home screen
- Items grouped by category, with checkboxes
- Quick-add bar with live suggestions as you type
- Share sheet: dedicated WhatsApp button, copy-to-clipboard "link", and
  share-as-plain-text
- Per-list options menu: rename, duplicate, delete
- Settings: dark mode toggle, optional splash screen, sharing-permission
  toggle (placeholder for a future real-time sync backend)

## Known limitation
There's no backend yet, so "sharing" sends a text snapshot of the list
(via WhatsApp, clipboard, or the system share sheet) rather than a live
link others can edit in real time. That would need a server — happy to
plan that out next if you want live collaborative lists.
