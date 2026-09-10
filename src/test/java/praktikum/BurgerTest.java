package praktikum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BurgerTest {

    // Константы для индексов ингредиентов (при стандартном порядке добавления)
    private static final int FILLING_INDEX = 0;
    private static final int SAUCE_INDEX = 1;
    private static final int EXTRA_INDEX = 2;

    // Константы для ожидаемых размеров списков
    private static final int EMPTY_SIZE = 0;
    private static final int SINGLE_SIZE = 1;

    // Константа для количества вхождений строки с булкой в чеке (header и footer)
    private static final int BUN_OCCURRENCES_IN_RECEIPT = 2;

    // Константы для некорректных индексов в тестах на исключения
    private static final int INVALID_INDEX_OUT_OF_BOUNDS = 5;
    private static final int INVALID_INDEX_TOO_LARGE = 10;

    private Burger burger;
    private Bun bunStub;
    private Ingredient fillingIngredient;
    private Ingredient sauceIngredient;
    private Ingredient extraFilling;

    @Before
    public void setUp() {
        burger = new Burger();

        bunStub = mock(Bun.class);
        when(bunStub.getPrice()).thenReturn(100f);
        when(bunStub.getName()).thenReturn("test bun");

        fillingIngredient = mock(Ingredient.class);
        when(fillingIngredient.getPrice()).thenReturn(50f);
        when(fillingIngredient.getName()).thenReturn("cutlet");
        when(fillingIngredient.getType()).thenReturn(IngredientType.FILLING);

        sauceIngredient = mock(Ingredient.class);
        when(sauceIngredient.getPrice()).thenReturn(30f);
        when(sauceIngredient.getName()).thenReturn("ketchup");
        when(sauceIngredient.getType()).thenReturn(IngredientType.SAUCE);

        extraFilling = mock(Ingredient.class);
        when(extraFilling.getPrice()).thenReturn(70f);
        when(extraFilling.getName()).thenReturn("cheese");
        when(extraFilling.getType()).thenReturn(IngredientType.FILLING);
    }

    // ---------- setBuns ----------
    @Test
    public void setBunsShouldSetBun() {
        burger.setBuns(bunStub);
        Assert.assertSame(bunStub, burger.bun);
    }

    // ---------- addIngredient ----------
    @Test
    public void addIngredientShouldIncreaseSize() {
        burger.addIngredient(fillingIngredient);
        Assert.assertEquals(SINGLE_SIZE, burger.ingredients.size());
    }

    @Test
    public void addIngredientShouldAddCorrectElement() {
        burger.addIngredient(fillingIngredient);
        Assert.assertSame(fillingIngredient, burger.ingredients.get(FILLING_INDEX));
    }

    // ---------- removeIngredient ----------
    @Test
    public void removeIngredientShouldDecreaseSize() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.removeIngredient(FILLING_INDEX);
        Assert.assertEquals(SINGLE_SIZE, burger.ingredients.size());
    }

    @Test
    public void removeIngredientShouldRemoveCorrectElement() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.removeIngredient(FILLING_INDEX);
        Assert.assertSame(sauceIngredient, burger.ingredients.get(FILLING_INDEX));
    }

    @Test
    public void removeIngredientShouldNotContainRemovedElement() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.removeIngredient(FILLING_INDEX);
        Assert.assertFalse(burger.ingredients.contains(fillingIngredient));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeIngredientWithInvalidIndexShouldThrowException() {
        int invalidIndex = EMPTY_SIZE; // список пуст, индекс 0 недопустим
        burger.removeIngredient(invalidIndex);
    }

    // ---------- moveIngredient ----------
    @Test
    public void moveIngredientShouldPlaceElementAtNewIndex() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(extraFilling);

        burger.moveIngredient(EXTRA_INDEX, FILLING_INDEX);
        Assert.assertSame(extraFilling, burger.ingredients.get(FILLING_INDEX));
    }

    @Test
    public void moveIngredientShouldShiftOtherElementsForward() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(extraFilling);

        burger.moveIngredient(EXTRA_INDEX, FILLING_INDEX);
        Assert.assertSame(fillingIngredient, burger.ingredients.get(SAUCE_INDEX));
        Assert.assertSame(sauceIngredient, burger.ingredients.get(EXTRA_INDEX));
    }

    @Test
    public void moveIngredientToLastPositionShouldPlaceElementAtEnd() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(extraFilling);

        burger.moveIngredient(FILLING_INDEX, EXTRA_INDEX);
        Assert.assertSame(fillingIngredient, burger.ingredients.get(EXTRA_INDEX));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveIngredientWithInvalidSourceIndexShouldThrowException() {
        burger.addIngredient(fillingIngredient);
        burger.moveIngredient(INVALID_INDEX_OUT_OF_BOUNDS, FILLING_INDEX);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveIngredientWithInvalidTargetIndexShouldThrowException() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.moveIngredient(FILLING_INDEX, INVALID_INDEX_TOO_LARGE);
    }

    // ---------- getReceipt ----------
    @Test
    public void getReceiptShouldContainBunHeader() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        String receipt = burger.getReceipt();
        Assert.assertTrue(receipt.contains("(==== test bun ===="));
    }

    @Test
    public void getReceiptShouldContainBunFooter() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        String receipt = burger.getReceipt();
        long count = receipt.lines()
                .filter(line -> line.contains("(==== test bun ===="))
                .count();
        Assert.assertEquals(BUN_OCCURRENCES_IN_RECEIPT, count);
    }

    @Test
    public void getReceiptShouldContainFillingLine() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        String receipt = burger.getReceipt();
        Assert.assertTrue(receipt.contains("= filling cutlet ="));
    }

    @Test
    public void getReceiptShouldContainSauceLine() {
        burger.setBuns(bunStub);
        burger.addIngredient(sauceIngredient);
        String receipt = burger.getReceipt();
        Assert.assertTrue(receipt.contains("= sauce ketchup ="));
    }

    @Test
    public void getReceiptShouldContainPriceLine() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        String receipt = burger.getReceipt();
        Assert.assertTrue(receipt.contains("Price:"));
    }

    @Test
    public void getReceiptWithoutIngredientsShouldHaveNoIngredientLines() {
        burger.setBuns(bunStub);
        String receipt = burger.getReceipt();
        long ingredientLines = receipt.lines()
                .filter(line -> line.startsWith("= "))
                .count();
        Assert.assertEquals(EMPTY_SIZE, ingredientLines);
    }

    @Test
    public void getReceiptWithoutIngredientsShouldStillContainPrice() {
        burger.setBuns(bunStub);
        String receipt = burger.getReceipt();
        Assert.assertTrue(receipt.contains("Price:"));
    }
}