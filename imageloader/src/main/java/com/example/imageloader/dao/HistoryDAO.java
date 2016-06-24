package com.example.imageloader.dao;

import com.example.imageloader.bean.HistoryUrl;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nBB on 16/6/22.
 */
public class HistoryDAO {

    private DbManager.DaoConfig daoConfig;

    public HistoryDAO() {

        daoConfig = new DbManager.DaoConfig()
                .setDbName("imageloader.db")
                .setDbVersion(1);
    }

    public List<HistoryUrl> getAll() {

        List<HistoryUrl> list = null;
        DbManager dbManager = x.getDb(daoConfig);

        try {
            list = dbManager.findAll(HistoryUrl.class);
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (list == null) {
                list = new ArrayList<HistoryUrl>();
            }
        }
        return list;
    }

    public void add(HistoryUrl historyUrl) {
        DbManager dbManager = x.getDb(daoConfig);
        try {
            dbManager.saveBindingId(historyUrl);
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                dbManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}