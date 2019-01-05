package in.oriange.iblebook.models;

public class FAQImageList {

    private String faqImageUrl;

    private String faqId;

    private String faqText;

    public String getFaqImageUrl ()
    {
        return faqImageUrl;
    }

    public void setFaqImageUrl (String faqImageUrl)
    {
        this.faqImageUrl = faqImageUrl;
    }

    public String getFaqId ()
    {
        return faqId;
    }

    public void setFaqId (String faqId)
    {
        this.faqId = faqId;
    }

    public String getFaqText ()
    {
        return faqText;
    }

    public void setFaqText (String faqText)
    {
        this.faqText = faqText;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [faqImageUrl = "+faqImageUrl+", faqId = "+faqId+", faqText = "+faqText+"]";
    }

}
