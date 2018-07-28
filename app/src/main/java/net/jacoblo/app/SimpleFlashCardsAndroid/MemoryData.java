package net.jacoblo.app.SimpleFlashCardsAndroid;

import java.util.ArrayList;

public class MemoryData {

    private ArrayList<FlashCard> m_Mem_data;
    private int[] m_Randoms;
    private int m_Pointer;

    public MemoryData () {
        m_Mem_data = new ArrayList<>();
        reset();
    }

    public int getCurrentNumber() {
        if (m_Pointer >= m_Mem_data.size()) return Integer.MAX_VALUE;
        return m_Randoms[m_Pointer] + 1;
    }

    public FlashCard getCurrentItem() {
        if (m_Pointer >= m_Mem_data.size()) return null;
        return m_Mem_data.get(m_Randoms[m_Pointer]);
    }

    public int itemLeft() {
        return m_Mem_data.size() - m_Pointer;
    }

    public boolean next() {
        if (m_Pointer >= m_Mem_data.size() ) return false;
        m_Pointer++;
        return true;
    }

    public void reset() {
        m_Randoms = new int[m_Mem_data.size()];
        for(int i = 0 ; i < m_Randoms.length ; i++ ) {
            m_Randoms[i] = i;
        }
        for (int i = 0 ; i < m_Randoms.length ; i++ ) {
            swap(m_Randoms, i, (int) (Math.random() * m_Randoms.length) );
        }
        m_Pointer = 0;
    }

    private void swap(int[] arr, int i, int j) {
        if (arr == null || i < 0 || j < 0 || i >= arr.length || j >= arr.length) return;
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static ArrayList<FlashCard> convertToMemData(ArrayList<String> inputs) {
        if (inputs == null || inputs.size() <= 0) return new ArrayList<>();

        ArrayList<FlashCard> resultCards = new ArrayList<>();
        for (String s : inputs) {
            FlashCard fc = FlashCard.convertCRVToFlashCard(s);
            if (fc != null) {
                resultCards.add(fc);
            }
        }

        return resultCards;
    }

    public ArrayList<FlashCard> get_mem_data() {
        return m_Mem_data;
    }

    public void set_mem_data(ArrayList<FlashCard> m_Mem_data) {
        this.m_Mem_data = m_Mem_data;
    }
}
