package praktikum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class BurgerTest {

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

    @Test
    public void setBunsShouldSetBun() {
        burger.setBuns(bunStub);
        Assert.assertSame(bunStub, burger.bun);
    }

    @Test
    public void addIngredientShouldAddToIngredientsList() {
        burger.addIngredient(fillingIngredient);
        Assert.assertEquals(1, burger.ingredients.size());
        Assert.assertSame(fillingIngredient, burger.ingredients.get(0));
    }

    @Test
    public void removeIngredientShouldRemoveByIndex() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(extraFilling);

        burger.removeIngredient(1);
        Assert.assertEquals(2, burger.ingredients.size());
        Assert.assertSame(fillingIngredient, burger.ingredients.get(0));
        Assert.assertSame(extraFilling, burger.ingredients.get(1));
        Assert.assertFalse(burger.ingredients.contains(sauceIngredient));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeIngredientWithInvalidIndexShouldThrowException() {
        burger.removeIngredient(0);
    }

    @Test
    public void moveIngredientShouldMoveToNewIndex() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(extraFilling);

        burger.moveIngredient(2, 0);

        Assert.assertSame(extraFilling, burger.ingredients.get(0));
        Assert.assertSame(fillingIngredient, burger.ingredients.get(1));
        Assert.assertSame(sauceIngredient, burger.ingredients.get(2));
    }

    @Test
    public void moveIngredientToLastPosition() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.addIngredient(extraFilling);

        burger.moveIngredient(0, 2);

        Assert.assertSame(sauceIngredient, burger.ingredients.get(0));
        Assert.assertSame(extraFilling, burger.ingredients.get(1));
        Assert.assertSame(fillingIngredient, burger.ingredients.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveIngredientWithInvalidSourceIndexShouldThrowException() {
        burger.addIngredient(fillingIngredient);
        burger.moveIngredient(5, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void moveIngredientWithInvalidTargetIndexShouldThrowException() {
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        burger.moveIngredient(0, 10);
    }

    // ---------- Тесты для getReceipt (каждый тест – одна проверка) ----------
    @Test
    public void getReceiptShouldContainBunHeaderAndFooter() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        String receipt = burger.getReceipt();

        Assert.assertTrue("Чек должен содержать шапку с именем булки",
                receipt.contains("(==== test bun ===="));
    }

    @Test
    public void getReceiptShouldContainIngredientLines() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        burger.addIngredient(sauceIngredient);
        String receipt = burger.getReceipt();

        Assert.assertTrue("Чек должен содержать строку для начинки",
                receipt.contains("= filling cutlet ="));
        Assert.assertTrue("Чек должен содержать строку для соуса",
                receipt.contains("= sauce ketchup ="));
    }

    @Test
    public void getReceiptShouldContainPriceLine() {
        burger.setBuns(bunStub);
        burger.addIngredient(fillingIngredient);
        String receipt = burger.getReceipt();

        Assert.assertTrue("Чек должен содержать строку с ценой",
                receipt.contains("Price:"));
    }

    @Test
    public void getReceiptWithoutIngredientsShouldContainOnlyBunsAndPrice() {
        burger.setBuns(bunStub);
        String receipt = burger.getReceipt();

        long ingredientLines = receipt.lines()
                .filter(line -> line.startsWith("= "))
                .count();
        Assert.assertEquals("Без ингредиентов строк с ними быть не должно", 0, ingredientLines);
        Assert.assertTrue("Цена должна присутствовать", receipt.contains("Price:"));
    }
}