package usbdescriptors;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.usb.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class usb4javaTest {

    private short vendorId;
    private short productId;
    private UsbServices services;
    private UsbDevice usbDevice;

    public UsbDevice findDevice(UsbHub hub, short vendorId, short productId) {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub()) {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }

    @Before
    public void setUp() throws UsbException {
        System.out.println(System.getProperty("os.name").toLowerCase());
        vendorId = 0x0403;
        productId = 0x6010;
        services = UsbHostManager.getUsbServices();
        usbDevice = findDevice(services.getRootUsbHub(), vendorId, productId);
        Assume.assumeNotNull(usbDevice);

    }

    @Test
    public void dumpService()  {
        System.out.println("USB Service Implementation: "
                + services.getImpDescription());
        System.out.println("Implementation version: "
                + services.getImpVersion());
        System.out.println("Service API version: " + services.getApiVersion());
        System.out.println();
    }

    @Test
    public void valid_vendor_id_and_product_id()  {

        System.out.println(usbDevice);
        assertThat(usbDevice.getUsbDeviceDescriptor().idVendor(), equalTo(vendorId));
        assertThat(usbDevice.getUsbDeviceDescriptor().idProduct(), equalTo(productId));
    }

    @Test
    public void read_device_descriptor()  {

        System.out.println(usbDevice.getUsbDeviceDescriptor().iSerialNumber());
        System.out.println(usbDevice.getUsbDeviceDescriptor().iManufacturer());
        System.out.println(usbDevice.getUsbDeviceDescriptor().iProduct());

        assertThat(usbDevice.getUsbDeviceDescriptor().iSerialNumber(), equalTo((byte) 0x00));
        assertThat(usbDevice.getUsbDeviceDescriptor().iManufacturer(), equalTo((byte) 0x1));
        assertThat(usbDevice.getUsbDeviceDescriptor().iProduct(), equalTo((byte) 0x02));
    }

    @Test(expected = UsbPlatformException.class)
    public void read_string_descriptor_but_happens_exception_in_windows() throws UsbException {
        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("win"));

        byte iProduct = usbDevice.getUsbDeviceDescriptor().iProduct();

        try {

            usbDevice.getUsbStringDescriptor(iProduct);
        } catch (UsbPlatformException e) {
            // The exception is raised when target usb device driver isn't WINUSB in case of Windows platformu.
            assertThat(e.toString(), containsString("javax.usb.UsbPlatformException: USB error 5: Can't open device Bus"));
            assertThat(e.toString(), containsString("Entity not found"));
            System.out.println(e);
            throw e;
        }
    }

    @Test
    public void read_string_descriptor_in_linux() throws UsbException, UnsupportedEncodingException {
        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("linux"));

        byte iProduct = usbDevice.getUsbDeviceDescriptor().iProduct();
        UsbStringDescriptor productString = usbDevice.getUsbStringDescriptor(iProduct);
        System.out.println("iProduct: " + iProduct);
        System.out.println(productString);

        assertThat(productString.getString(), equalTo("Dual RS232-HS"));
    }
}
