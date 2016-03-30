package com.eggheadgames.realmassethelper;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;


@RunWith(MockitoJUnitRunner.class)
public class RealmAssetHelperTest {

    @Mock
    private Context context;

    @Mock
    private OsUtil osUtil;

    private RealmAssetHelper realmAssetHelper;

    @Before
    public void prepareForTest() {
        realmAssetHelper = spy(new RealmAssetHelper());
        realmAssetHelper.mContext = context;
    }

    @Test(expected = RuntimeException.class)
    public void onEmptyDatabaseName_shouldThrowRuntimeException() throws RuntimeException{
        realmAssetHelper.loadDatabaseToStorage(null);
    }

    @Test(expected = RuntimeException.class)
    public void onMissingDbAsset_shouldThrowRuntimeException() throws RuntimeException{
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
    }


    @Test
    public void onFreshInstall_databaseVersionShouldBeStored() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(null);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class))).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(2);
    }

    @Test
    public void onDatabaseUpdate_databaseVersionShouldBeStored() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class))).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(2);
    }

    @Test
    public void onFreshAppInstall_databaseShouldBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(null);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onDatabaseUpdate_databaseShouldBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class))).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onSameDatabaseVersionArrived_databaseShouldNotBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(2);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class))).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
        Mockito.verify(osUtil, Mockito.never()).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onFreshAppInstall_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperListener listener = Mockito.mock(IRealmAssetHelperListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(null);

        realmAssetHelper.setListener(listener);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(listener, Mockito.times(1)).onFreshInstall();
    }

    @Test
    public void onDatabaseUpdate_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperListener listener = Mockito.mock(IRealmAssetHelperListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class))).thenReturn(2);

        realmAssetHelper.setListener(listener);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(listener, Mockito.times(1)).onUpdated();
    }

    @Test
    public void onSameDatabaseVersionArrived_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperListener listener = Mockito.mock(IRealmAssetHelperListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class))).thenReturn(2);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class))).thenReturn(2);

        realmAssetHelper.setListener(listener);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
        Mockito.verify(listener, Mockito.times(1)).onUpdateIgnored();
    }






}