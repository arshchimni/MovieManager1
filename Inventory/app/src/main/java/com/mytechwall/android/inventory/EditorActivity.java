package com.mytechwall.android.inventory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mytechwall.android.inventory.data.InventoryContract;
import com.mytechwall.android.inventory.presenter.ImageUtils;

import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int PICK_IMAGE_REQUEST = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private Bitmap imageBitmap;

    private EditText itemName;
    private EditText quantity;
    private EditText supplierName;
    private EditText supplierEmail;
    private EditText supplierNumber;
    private ImageView itemImage;
    private LinearLayout mainContainer;
    private boolean mItemHasChanged = false;
    private Uri sendUri;

    private View.OnTouchListener mOnClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        itemName = (EditText) findViewById(R.id.nameText);
        quantity = (EditText) findViewById(R.id.quanityInput);
        supplierName = (EditText) findViewById(R.id.SupplierNameInput);
        supplierEmail = (EditText) findViewById(R.id.supplierEmailInput);
        supplierNumber = (EditText) findViewById(R.id.supplierNumberInput);
        itemImage = (ImageView) findViewById(R.id.displayImage);
        mainContainer = (LinearLayout) findViewById(R.id.mainContainer);

        itemImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                getPermission();

                return false;
            }
        });

        quantity.setOnTouchListener(mOnClickListener);
        supplierName.setOnTouchListener(mOnClickListener);
        supplierEmail.setOnTouchListener(mOnClickListener);
        supplierNumber.setOnTouchListener(mOnClickListener);
        itemName.setOnTouchListener(mOnClickListener);

        Intent intent = getIntent();
        sendUri = intent.getData();
        if (sendUri == null) {
            setTitle("Add new item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit item");
            getSupportLoaderManager().initLoader(0, null, this);

        }

    }

    public void getPermission() {
        //want to request permission for only devices with api 23 and higher hence the need for check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            } else {
                gaalleryIntent();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    gaalleryIntent();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {
                    Snackbar.make(mainContainer, "Cannot upload picture", Snackbar.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    public void gaalleryIntent() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                itemImage.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (sendUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                insertItem();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonOnClickListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedConfirmationDialog(discardButtonOnClickListner);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertItem() {
        String nameItem = itemName.getText().toString().trim();
        String itemQuantity = (quantity.getText().toString().trim());
        String suppName = supplierName.getText().toString().trim();
        String suppEmail = supplierEmail.getText().toString().trim();
        String suppNumber = supplierNumber.getText().toString().trim();
        int quantity;
        if (itemQuantity.isEmpty()) {
            quantity = 0;
        } else {
            quantity = Integer.parseInt(itemQuantity);
        }


        if (TextUtils.isEmpty(nameItem) && TextUtils.isEmpty(suppName) && TextUtils.isEmpty(suppEmail) &&
                TextUtils.isEmpty(suppNumber) && TextUtils.isEmpty(itemQuantity)) {
            finish();
            return;
        }


        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.COLUMN_NAME, nameItem);
        values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, quantity);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_EMAIL, suppEmail);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER, suppNumber);
        values.put(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME, suppName);
        if (imageBitmap != null) {
            byte[] imagebyte = ImageUtils.getImageBytes(imageBitmap);
            values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE, imagebyte);
        }
        if (sendUri == null) {
            Uri newRow = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);
            if (newRow != null) {
                //refrence of the layout in which the snackbar will be displayed
                Snackbar.make(mainContainer, "Item saved sucessfully", Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                        super.onDismissed(transientBottomBar, event);
                    }
                }).show();
            } else {
                Snackbar.make(mainContainer, "Error saving item", Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                        super.onDismissed(transientBottomBar, event);
                    }
                }).show();
            }
        } else {
            int affectedItems = getContentResolver().update(sendUri, values, null, null);
            if (affectedItems != 0) {
                //refrence of the layout in which the snackbar will be displayed
                Snackbar.make(mainContainer, "Item saved sucessfully", Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                        super.onDismissed(transientBottomBar, event);
                    }
                }).show();
            } else {
                Snackbar.make(mainContainer, "Error saving item", Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        finish();
                        super.onDismissed(transientBottomBar, event);
                    }
                }).show();
            }
        }


    }

    public void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (sendUri != null) {
            int rowsDeleted = getContentResolver().delete(sendUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deletion not possible", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deleted item", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showUnsavedConfirmationDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedConfirmationDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_EMAIL,
                InventoryContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME

        };

        return new CursorLoader(this, sendUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int itemNameIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME);
            int itemNameImageIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_IMAGE);
            int supplierNameIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_NAME);
            int quantityIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
            int supplierEmailIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_EMAIL);
            int supplierContactNumberIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SUPPLIER_CONTACT_NUMBER);

            String itemNameGet = data.getString(itemNameIndex);
            byte[] itemImageGet = data.getBlob(itemNameImageIndex);
            int quantityGet = data.getInt(quantityIndex);
            String supplierNameGet = data.getString(supplierNameIndex);
            String supplierEmailGet = data.getString(supplierEmailIndex);
            String supplierContactGet = data.getString(supplierContactNumberIndex);

            itemName.setText(itemNameGet);
            if (itemImageGet != null) {
                Bitmap image = ImageUtils.getImageBitmap(itemImageGet);
                itemImage.setImageBitmap(image);
            }
            quantity.setText(String.valueOf(quantityGet));
            supplierEmail.setText(supplierEmailGet);
            supplierName.setText(supplierNameGet);
            supplierNumber.setText(supplierContactGet);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemName.setText("");
        //itemImage.setImageBitmap(null);
        quantity.setText(String.valueOf(""));
        supplierEmail.setText("");
        supplierName.setText("");
        supplierNumber.setText("");
    }
}
