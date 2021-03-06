package seedu.address.commons.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.model.AddressBook;
import seedu.address.model.food.UniqueFoodList;
import seedu.address.model.tag.UniqueTagList;
import seedu.address.model.user.UserProfile;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.XmlAdaptedAllergy;
import seedu.address.storage.XmlAdaptedFood;
import seedu.address.storage.XmlAdaptedTag;
import seedu.address.storage.XmlSerializableAddressBook;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.FoodBuilder;
import seedu.address.testutil.TestUtil;

public class XmlUtilTest {

    private static final String TEST_DATA_FOLDER = FileUtil.getPath("src/test/data/XmlUtilTest/");
    private static final File EMPTY_FILE = new File(TEST_DATA_FOLDER + "empty.xml");
    private static final File MISSING_FILE = new File(TEST_DATA_FOLDER + "missing.xml");
    private static final File VALID_FILE = new File(TEST_DATA_FOLDER + "validAddressBook.xml");
    private static final File MISSING_FOOD_FIELD_FILE = new File(TEST_DATA_FOLDER + "missingFoodField.xml");
    private static final File INVALID_FOOD_FIELD_FILE = new File(TEST_DATA_FOLDER + "invalidFoodField.xml");
    private static final File VALID_FOOD_FILE = new File(TEST_DATA_FOLDER + "validFood.xml");
    private static final File TEMP_FILE = new File(TestUtil.getFilePathInSandboxFolder("tempAddressBook.xml"));

    private static final String INVALID_PHONE = "9482asf424";

    private static final String VALID_NAME = "Hans Muster";
    private static final String VALID_PHONE = "9482424";
    private static final String VALID_EMAIL = "hans@example";
    private static final String VALID_ADDRESS = "4th street";
    private static final String VALID_PRICE = "$0";
    private static final String VALID_RATING = "0";
    private static final List<XmlAdaptedTag> VALID_TAGS = Collections.singletonList(new XmlAdaptedTag("friends"));
    private static final List<XmlAdaptedAllergy> VALID_ALLERGIES = Collections.singletonList(
            new XmlAdaptedAllergy("lactose"));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getDataFromFile_nullFile_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.getDataFromFile(null, AddressBook.class);
    }

    @Test
    public void getDataFromFile_nullClass_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.getDataFromFile(VALID_FILE, null);
    }

    @Test
    public void getDataFromFile_missingFile_fileNotFoundException() throws Exception {
        thrown.expect(FileNotFoundException.class);
        XmlUtil.getDataFromFile(MISSING_FILE, AddressBook.class);
    }

    @Test
    public void getDataFromFile_emptyFile_dataFormatMismatchException() throws Exception {
        thrown.expect(JAXBException.class);
        XmlUtil.getDataFromFile(EMPTY_FILE, AddressBook.class);
    }

    @Test
    public void getDataFromFile_validFile_validResult() throws Exception {
        AddressBook dataFromFile = XmlUtil.getDataFromFile(VALID_FILE, XmlSerializableAddressBook.class).toModelType();
        assertEquals(9, dataFromFile.getFoodList().size());
        assertEquals(0, dataFromFile.getTagList().size());
    }

    @Test
    public void xmlAdaptedFoodFromFile_fileWithMissingFoodField_validResult() throws Exception {
        XmlAdaptedFood actualFood = XmlUtil.getDataFromFile(
                MISSING_FOOD_FIELD_FILE, XmlAdaptedFoodWithRootElement.class);
        XmlAdaptedFood expectedFood = new XmlAdaptedFood(
                null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_PRICE, VALID_RATING, VALID_TAGS, VALID_ALLERGIES);
        assertEquals(expectedFood, actualFood);
    }

    @Test
    public void xmlAdaptedFoodFromFile_fileWithInvalidFoodField_validResult() throws Exception {
        XmlAdaptedFood actualFood = XmlUtil.getDataFromFile(
                INVALID_FOOD_FIELD_FILE, XmlAdaptedFoodWithRootElement.class);
        XmlAdaptedFood expectedFood = new XmlAdaptedFood(
                VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_PRICE, VALID_RATING,
                VALID_TAGS, VALID_ALLERGIES);
        assertEquals(expectedFood, actualFood);
    }

    @Test
    public void xmlAdaptedFoodFromFile_fileWithValidFood_validResult() throws Exception {
        XmlAdaptedFood actualFood = XmlUtil.getDataFromFile(
                VALID_FOOD_FILE, XmlAdaptedFoodWithRootElement.class);
        XmlAdaptedFood expectedFood = new XmlAdaptedFood(
                VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_PRICE, VALID_RATING,
                VALID_TAGS, VALID_ALLERGIES);
        assertEquals(expectedFood, actualFood);
    }

    @Test
    public void saveDataToFile_nullFile_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.saveDataToFile(null, new AddressBook());
    }

    @Test
    public void saveDataToFile_nullClass_throwsNullPointerException() throws Exception {
        thrown.expect(NullPointerException.class);
        XmlUtil.saveDataToFile(VALID_FILE, null);
    }

    @Test
    public void saveDataToFile_missingFile_fileNotFoundException() throws Exception {
        thrown.expect(FileNotFoundException.class);
        XmlUtil.saveDataToFile(MISSING_FILE, new AddressBook());
    }

    @Test
    public void saveDataToFile_validFile_dataSaved() throws Exception {
        TEMP_FILE.createNewFile();
        XmlSerializableAddressBook dataToWrite = new XmlSerializableAddressBook(new AddressBookStub());
        XmlUtil.saveDataToFile(TEMP_FILE, dataToWrite);
        XmlSerializableAddressBook dataFromFile = XmlUtil.getDataFromFile(TEMP_FILE, XmlSerializableAddressBook.class);
        assertEquals(dataToWrite, dataFromFile);

        AddressBookBuilder builder = new AddressBookBuilder(new AddressBookStub());
        dataToWrite = new XmlSerializableAddressBook(
                builder.withFood(new FoodBuilder().build()).withTag("Friends").build());

        XmlUtil.saveDataToFile(TEMP_FILE, dataToWrite);
        dataFromFile = XmlUtil.getDataFromFile(TEMP_FILE, XmlSerializableAddressBook.class);
        assertEquals(dataToWrite, dataFromFile);
    }

    /**
     * Test class annotated with {@code XmlRootElement} to allow unmarshalling of .xml data to {@code XmlAdaptedFood}
     * objects.
     */
    @XmlRootElement(name = "food")
    private static class XmlAdaptedFoodWithRootElement extends XmlAdaptedFood {}

    /**
     * An AddressBookStub with a default user profile
     */
    private class AddressBookStub extends AddressBook {
        final UniqueFoodList foods;
        final UniqueTagList tags;
        private UserProfile profile;

        public AddressBookStub() {
            this.foods = new UniqueFoodList();
            this.tags = new UniqueTagList();
            this.profile = SampleDataUtil.getSampleProfile();
        }

        @Override
        public UserProfile getUserProfile() {
            return profile;
        }
    }
}
