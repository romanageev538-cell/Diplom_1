package praktikum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BurgerTest {

    private Burger burger;
    private Bun bunStub;
    private Ingredient ingredient1;
    private Ingredient ingredient2;
    private Ingredient ingredient3;

    // Параметры для параметризованного теста: ожидаемая цена при разных комбинациях
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {100f, 50f, 60f, 70f, 100 * 2 + 50 + 60 + 70}, // bun=100, 3 ингредиента
                {200f, 0f, 0f, 0f, 200 * 2},                // bun=200, без ингредиентов
                {50f, 10f, 20f, 0f, 50 * 2 + 10 + 20}       // bun=50, 2 ингредиента
        });
    }

    private final float bunPrice;
    private final float ing1Price;
    private final float ing2Price;
    private final float ing3Price;
    private final float expectedTotalPrice;

    public BurgerTest(float bunPrice, float ing1Price, float ing2Price, float ing3Price, float expectedTotalPrice) {
        this.bunPrice = bunPrice;
        this.ing1Price = ing1Price;
        this.ing2Price = ing2Price;
        this.ing3Price = ing3Price;
        this.expectedTotalPrice = expectedTotalPrice;
    }

    @Before
    public void setUp() {
        burger = new Burger();

        // Стаб для булки: фиксируем цену и имя
        bunStub = Mockito.mock(Bun.class);
        Mockito.when(bunStub.getPrice()).thenReturn(bunPrice);
        Mockito.when(bunStub.getName()).thenReturn("test bun");

        // Стабы для ингредиентов
        ingredient1 = Mockito.mock(Ingredient.class);
        Mockito.when(ingredient1.getPrice()).thenReturn(ing1Price);
        Mockito.when(ingredient1.getName()).thenReturn("ingredient1");
        Mockito.when(ingredient1.getType()).thenReturn(IngredientType.FILLING);

        ingredient2 = Mockito.mock(Ingredient.class);
        Mockito.when(ingredient2.getPrice()).thenReturn(ing2Price);
        Mockito.when(ingredient2.getName()).thenReturn("ingredient2");
        Mockito.when(ingredient2.getType()).thenReturn(IngredientType.SAUCE);

        ingredient3 = Mockito.mock(Ingredient.class);
        Mockito.when(ingredient3.getPrice()).thenReturn(ing3Price);
        Mockito.when(ingredient3.getName()).thenReturn("ingredient3");
        Mockito.when(ingredient3.getType()).thenReturn(IngredientType.FILLING);
    }

    @Test
    public void testSetBuns_and_getPrice_with_parameters() {
        burger.setBuns(bunStub);

        if (ing1Price > 0) burger.addIngredient(ingredient1);
        if (ing2Price > 0) burger.addIngredient(ingredient2);
        if (ing3Price > 0) burger.addIngredient(ingredient3);

        float actualPrice = burger.getPrice();
        Assert.assertEquals(expectedTotalPrice, actualPrice, 0.01f);
    }

    @Test
    public void testAddAndRemoveIngredient() {
        burger.setBuns(bunStub);
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);

        Assert.assertEquals(2, burger.ingredients.size());

        burger.removeIngredient(0); // удаляем первый
        Assert.assertEquals(1, burger.ingredients.size());
        Assert.assertSame(ingredient2, burger.ingredients.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredient_invalidIndex_throws() {
        burger.setBuns(bunStub);
        // пустой список
        burger.removeIngredient(0);
    }

    @Test
    public void testMoveIngredient() {
        burger.setBuns(bunStub);
        burger.addIngredient(ingredient1); // index 0
        burger.addIngredient(ingredient2); // index 1
        burger.addIngredient(ingredient3); // index 2

        // перемещаем элемент с индексом 2 на позицию 0
        burger.moveIngredient(2, 0);

        Assert.assertSame(ingredient3, burger.ingredients.get(0));
        Assert.assertSame(ingredient1, burger.ingredients.get(1));
        Assert.assertSame(ingredient2, burger.ingredients.get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredient_invalidSourceIndex_throws() {
        burger.setBuns(bunStub);
        burger.addIngredient(ingredient1);
        burger.moveIngredient(5, 0); // неверный исходный индекс
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredient_invalidTargetIndex_throws() {
        burger.setBuns(bunStub);
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);
        burger.moveIngredient(0, 10); // неверный целевой индекс
    }

    @Test
    public void testGetReceipt_formatAndPrice() {
        burger.setBuns(bunStub);
        burger.addIngredient(ingredient1);
        burger.addIngredient(ingredient2);

        String receipt = burger.getReceipt();

        // Проверяем шапку и подвал чека (имя булки)
        Assert.assertTrue(receipt.contains("(==== test bun ===="));

        // Проверяем строки ингредиентов (формат: "= тип ингредиент =")
        Assert.assertTrue(receipt.contains("= filling ingredient1 ="));
        Assert.assertTrue(receipt.contains("= sauce ingredient2 ="));

        // Проверяем, что в чеке есть цена
        Assert.assertTrue(receipt.contains("Price:"));
    }

}
