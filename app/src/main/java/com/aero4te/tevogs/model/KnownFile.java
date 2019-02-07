package com.aero4te.tevogs.model;

public enum KnownFile {
    NFC_LAST_CONFIG {
        @Override
        public String[] getParentFolders() { return null; }

        @Override
        public String getDir() { return "config"; }

        @Override
        public String getFilename() { return "config.txt"; }

        @Override
        public boolean getAppend() {
            return false;
        }

        @Override
        public String getFilePath() {
            return "/config/config.txt";
        }

        @Override
        public String getFolderPath() {
            return "/config/";
        }
    },
    TAG_AES_KEY {
        @Override
        public String[] getParentFolders() { return null; }

        @Override
        public String getDir() { return "aes"; }

        @Override
        public String getFilename() { return "key.txt"; }

        @Override
        public boolean getAppend() {
            return false;
        }

        @Override
        public String getFilePath() {
            return "/aes/key.txt";
        }

        @Override
        public String getFolderPath() {
            return "/aes/";
        }
    },
    TAG_HISTORY {
        @Override
        public String[] getParentFolders() { return new String[] {"tag"}; }

        @Override
        public String getDir() { return "history"; }

        @Override
        public String getFilename() { return "log.txt"; }

        @Override
        public boolean getAppend() {
            return true;
        }

        @Override
        public String getFilePath() {
            return "/tag/history/log.txt";
        }

        @Override
        public String getFolderPath() {
            return "/tag/history/";
        }
    };
    public abstract String[] getParentFolders();
    public abstract String getDir();
    public abstract String getFilename();
    public abstract boolean getAppend();
    public abstract String getFilePath();
    public abstract String getFolderPath();
}
