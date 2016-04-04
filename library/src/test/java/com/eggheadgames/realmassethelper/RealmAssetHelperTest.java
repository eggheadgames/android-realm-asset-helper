package com.eggheadgames.realmassethelper;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import io.realm.RealmConfiguration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


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

        Mockito.when(osUtil.isEmpty(Mockito.anyString())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                String string = (String) invocation.getArguments()[0];
                return string == null || string.isEmpty();
            }
        });
        Mockito.when(osUtil.isDatabaseAssetExists(Mockito.any(Context.class), Mockito.anyString())).thenReturn(true);

        Mockito.when(osUtil.loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString())).thenReturn(TestConstants.FILE_PATH);

        Mockito.when(osUtil.generateDatabaseFileName(Mockito.any(Context.class), Mockito.anyString())).thenReturn(TestConstants.FILE_PATH);

        Mockito.when(osUtil.getFileNameForDatabase(Mockito.any(Context.class), Mockito.anyString())).thenReturn(TestConstants.FILE_PATH);

        realmAssetHelper.mOsUtil = osUtil;
    }

    @Test(expected = RuntimeException.class)
    public void onEmptyDatabaseName_shouldThrowRuntimeException() throws RuntimeException{
        realmAssetHelper.loadDatabaseToStorage(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void onMissingDbAsset_shouldThrowRuntimeException() throws RuntimeException{
        Mockito.when(osUtil.isDatabaseAssetExists(context, TestConstants.DB_NAME)).thenReturn(false);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);
    }

    @Test
    public void onFreshInstall_databaseVersionShouldBeStored() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);

        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(context, 2, TestConstants.DB_NAME);
    }

    @Test
    public void onDatabaseUpdate_databaseVersionShouldBeStored() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);

        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(context, 2, TestConstants.DB_NAME);
    }

    @Test
    public void onFreshAppInstall_databaseShouldBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);

        Mockito.verify(osUtil, Mockito.times(1)).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onDatabaseUpdate_databaseShouldBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);

        Mockito.verify(osUtil, Mockito.times(1)).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onSameDatabaseVersionArrived_databaseShouldNotBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);
        Mockito.verify(osUtil, Mockito.never()).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onFreshAppInstall_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperStorageListener listener = Mockito.mock(IRealmAssetHelperStorageListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, listener);

        Mockito.verify(listener, Mockito.times(1)).onLoadedToStorage(TestConstants.FILE_PATH, RealmAssetHelperStatus.INSTALLED);
    }

    @Test
    public void onDatabaseUpdate_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperStorageListener listener = Mockito.mock(IRealmAssetHelperStorageListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, listener);

        Mockito.verify(listener, Mockito.times(1)).onLoadedToStorage(TestConstants.FILE_PATH, RealmAssetHelperStatus.UPDATED);
    }

    @Test
    public void onSameDatabaseVersionArrived_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperStorageListener listener = Mockito.mock(IRealmAssetHelperStorageListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, listener);
        Mockito.verify(listener, Mockito.times(1)).onLoadedToStorage(TestConstants.FILE_PATH, RealmAssetHelperStatus.IGNORED);
    }

    @Test
    public void onDatabaseUpdate_versionShouldBeSetForCorrectDatabase() {

        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);
        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(context, 2, TestConstants.DB_NAME);
    }

    @Test(expected = RuntimeException.class)
    public void onLoadDatabaseToStorageFileNotFound_exceptionShouldBeThrown() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        when(osUtil.loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME, null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = RuntimeException.class)
    public void onLoadDatabaseWithRealmError_exceptionShouldBeThrown() {
        when(osUtil.createDatabaseFromLoadedFile(any(RealmConfiguration.class))).thenThrow(RuntimeException.class);

        realmAssetHelper.loadDatabase(TestConstants.DB_NAME, any(RealmConfiguration.class), null);
    }
}