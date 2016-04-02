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

        Mockito.when(osUtil.isEmpty(Mockito.anyString())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                String string = (String) invocation.getArguments()[0];
                return string == null || string.isEmpty();
            }
        });
        Mockito.when(osUtil.isDatabaseAssetExists(Mockito.any(Context.class), Mockito.anyString())).thenReturn(true);
        realmAssetHelper.mOsUtil = osUtil;
    }

    @Test(expected = RuntimeException.class)
    public void onEmptyDatabaseName_shouldThrowRuntimeException() throws RuntimeException{
        realmAssetHelper.loadDatabaseToStorage(null);
    }

    @Test(expected = RuntimeException.class)
    public void onMissingDbAsset_shouldThrowRuntimeException() throws RuntimeException{
        Mockito.when(osUtil.isDatabaseAssetExists(context, TestConstants.DB_NAME)).thenReturn(false);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
    }

    @Test
    public void onFreshInstall_databaseVersionShouldBeStored() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(context, 2, TestConstants.DB_NAME);
    }

    @Test
    public void onDatabaseUpdate_databaseVersionShouldBeStored() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(context, 2, TestConstants.DB_NAME);
    }

    @Test
    public void onFreshAppInstall_databaseShouldBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onDatabaseUpdate_databaseShouldBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(osUtil, Mockito.times(1)).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onSameDatabaseVersionArrived_databaseShouldNotBeLoadedToInternalStorage() {
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
        Mockito.verify(osUtil, Mockito.never()).loadDatabaseToLocalStorage(Mockito.any(Context.class), Mockito.anyString());
    }

    @Test
    public void onFreshAppInstall_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperListener listener = Mockito.mock(IRealmAssetHelperListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(null);

        realmAssetHelper.setListener(listener);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(listener, Mockito.times(1)).onFreshInstall();
    }

    @Test
    public void onDatabaseUpdate_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperListener listener = Mockito.mock(IRealmAssetHelperListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.setListener(listener);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);

        Mockito.verify(listener, Mockito.times(1)).onUpdated();
    }

    @Test
    public void onSameDatabaseVersionArrived_relevantCallbackShouldBeTriggered() {
        IRealmAssetHelperListener listener = Mockito.mock(IRealmAssetHelperListener.class);
        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.setListener(listener);
        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
        Mockito.verify(listener, Mockito.times(1)).onUpdateIgnored();
    }

    @Test
    public void onDatabaseUpdate_versionShouldBeSetForCorrectDatabase() {

        Mockito.when(osUtil.getCurrentDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(1);
        Mockito.when(osUtil.getAssetsDbVersion(Mockito.any(Context.class), Mockito.anyString())).thenReturn(2);

        realmAssetHelper.loadDatabaseToStorage(TestConstants.DB_NAME);
        Mockito.verify(osUtil, Mockito.times(1)).storeDatabaseVersion(context, 2, TestConstants.DB_NAME);

    }
}